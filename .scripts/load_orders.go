// load_orders.go
package main

import (
	"bytes"
	"context"
	"encoding/json"
	"flag"
	"fmt"
	"math/rand"
	"net/http"
	"sync"
	"sync/atomic"
	"time"
)

type OrderItem struct {
	ProductID string  `json:"productId"`
	Quantity  int     `json:"quantity"`
	Price     float64 `json:"price"`
}

type OrderRequest struct {
	CustomerID string      `json:"customerId"`
	Items      []OrderItem `json:"items"`
}

type Stats struct {
	totalReq   int64
	successReq int64
	failReq    int64
	totalLatNs int64
}

func main() {
	url := flag.String("url", "http://localhost:8080/orders", "target URL")
	rps := flag.Int("rps", 100, "requests per second")
	duration := flag.Duration("duration", 30*time.Second, "test duration")
	workers := flag.Int("workers", 20, "number of workers")
	flag.Parse()

	client := &http.Client{
		Timeout: 5 * time.Second,
	}

	var stats Stats

	ctx, cancel := context.WithTimeout(context.Background(), *duration)
	defer cancel()

	tokens := make(chan struct{}, *rps)

	// rate limiter
	go func() {
		ticker := time.NewTicker(time.Second / time.Duration(*rps))
		defer ticker.Stop()

		for {
			select {
			case <-ctx.Done():
				close(tokens)
				return
			case <-ticker.C:
				select {
				case tokens <- struct{}{}:
				default:
				}
			}
		}
	}()

	var wg sync.WaitGroup

	for i := 0; i < *workers; i++ {
		wg.Add(1)

		go func() {
			defer wg.Done()

			for range tokens {

				start := time.Now()

				body := buildOrder()

				reqBody, _ := json.Marshal(body)

				req, err := http.NewRequest("POST", *url, bytes.NewBuffer(reqBody))
				if err != nil {
					atomic.AddInt64(&stats.failReq, 1)
					continue
				}

				req.Header.Set("Content-Type", "application/json")

				resp, err := client.Do(req)
				lat := time.Since(start)

				atomic.AddInt64(&stats.totalReq, 1)
				atomic.AddInt64(&stats.totalLatNs, lat.Nanoseconds())

				if err != nil {
					atomic.AddInt64(&stats.failReq, 1)
					continue
				}

				resp.Body.Close()

				if resp.StatusCode >= 200 && resp.StatusCode < 300 {
					atomic.AddInt64(&stats.successReq, 1)
				} else {
					atomic.AddInt64(&stats.failReq, 1)
				}
			}
		}()
	}

	// reporter
	go func() {
		ticker := time.NewTicker(2 * time.Second)
		defer ticker.Stop()

		for range ticker.C {

			total := atomic.LoadInt64(&stats.totalReq)
			success := atomic.LoadInt64(&stats.successReq)
			fail := atomic.LoadInt64(&stats.failReq)
			lat := atomic.LoadInt64(&stats.totalLatNs)

			if total == 0 {
				continue
			}

			avg := time.Duration(lat / total)

			fmt.Printf(
				"requests=%d success=%d fail=%d avg_latency=%s\n",
				total,
				success,
				fail,
				avg,
			)
		}
	}()

	wg.Wait()

	total := atomic.LoadInt64(&stats.totalReq)
	success := atomic.LoadInt64(&stats.successReq)
	fail := atomic.LoadInt64(&stats.failReq)
	lat := atomic.LoadInt64(&stats.totalLatNs)

	var avg time.Duration
	if total > 0 {
		avg = time.Duration(lat / total)
	}

	fmt.Println("----- FINAL RESULT -----")
	fmt.Printf("total_requests: %d\n", total)
	fmt.Printf("success: %d\n", success)
	fmt.Printf("fail: %d\n", fail)
	fmt.Printf("avg_latency: %s\n", avg)
	fmt.Printf("throughput: %.2f req/s\n", float64(total)/(*duration).Seconds())
}

func buildOrder() OrderRequest {
	return OrderRequest{
		CustomerID: randomUUID(),
		Items: []OrderItem{
			{
				ProductID: randomUUID(),
				Quantity:  1,
				Price:     rand.Float64()*100 + 1,
			},
		},
	}
}

func randomUUID() string {
	return fmt.Sprintf(
		"%08x-%04x-%04x-%04x-%012x",
		rand.Uint32(),
		rand.Uint32()&0xffff,
		rand.Uint32()&0xffff,
		rand.Uint32()&0xffff,
		rand.Uint32(),
	)
}
