// Package aftermath provides fail-open, zero-overhead incident capture and redaction SDK for Go HTTP applications.
package aftermath

import (
	"bytes"
	"encoding/json"
	"fmt"
	"net/http"
	"strings"
	"time"

	"github.com/google/uuid"
)

type DeploymentInfo struct {
	ServiceName    string `json:"serviceName"`
	ServiceVersion string `json:"serviceVersion"`
	Environment    string `json:"environment"`
	CommitHash     string `json:"commitHash"`
}

type RequestSnapshot struct {
	Method      string            `json:"method"`
	URI         string            `json:"uri"`
	QueryParams map[string]string `json:"queryParams"`
	Headers     map[string]string `json:"headers"`
	Body        string            `json:"body"`
	Timestamp   int64             `json:"timestamp"`
}

type ErrorSnapshot struct {
	ExceptionClass string `json:"exceptionClass"`
	Message        string `json:"message"`
	StackTrace     string `json:"stackTrace"`
	StatusCode     int    `json:"statusCode"`
}

type IncidentEvent struct {
	IncidentId string          `json:"incidentId"`
	TraceId    string          `json:"traceId"`
	Timestamp  int64           `json:"timestamp"`
	Request    RequestSnapshot `json:"request"`
	Error      ErrorSnapshot   `json:"error"`
	Deployment DeploymentInfo  `json:"deployment"`
}

type SDK struct {
	CollectorURL string
	ServiceName  string
	HTTPClient   *http.Client
}

func NewSDK(collectorURL, serviceName string) *SDK {
	if collectorURL == "" {
		collectorURL = "http://localhost:8090/api/v1/incidents"
	}
	return &SDK{
		CollectorURL: collectorURL,
		ServiceName:  serviceName,
		HTTPClient:   &http.Client{Timeout: 2 * time.Second},
	}
}

func (s *SDK) RedactHeaders(headers http.Header) map[string]string {
	sensitive := map[string]bool{
		"authorization":       true,
		"cookie":              true,
		"x-api-key":           true,
		"proxy-authorization": true,
	}
	redacted := make(map[string]string)
	for k, v := range headers {
		if sensitive[strings.ToLower(k)] {
			redacted[k] = "[REDACTED]"
		} else if len(v) > 0 {
			redacted[k] = v[0]
		}
	}
	return redacted
}

func (s *SDK) Middleware(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w *http.ResponseWriter, r *http.Request) {
		defer func() {
			if rerr := recover(); rerr != nil {
				s.captureAsync(r, fmt.Sprintf("%v", rerr))
				panic(rerr) // Re-throw after capturing
			}
		}()
		next.ServeHTTP(*w, r)
	})
}

func (s *SDK) captureAsync(r *http.Request, errStr string) {
	go func() {
		defer func() { recover() }() // Fail-open protection
		event := IncidentEvent{
			IncidentId: uuid.New().String(),
			TraceId:    r.Header.Get("X-Trace-Id"),
			Timestamp:  time.Now().UnixNano() / int64(time.Millisecond),
			Request: RequestSnapshot{
				Method:    r.Method,
				URI:       r.URL.RequestURI(),
				Headers:   s.RedactHeaders(r.Header),
				Timestamp: time.Now().UnixNano() / int64(time.Millisecond),
			},
			Error: ErrorSnapshot{
				ExceptionClass: "PanicError",
				Message:        errStr,
				StackTrace:     errStr,
				StatusCode:     500,
			},
			Deployment: DeploymentInfo{
				ServiceName:    s.ServiceName,
				ServiceVersion: "1.0.0",
				Environment:    "production",
				CommitHash:     "go-commit",
			},
		}

		if event.TraceId == "" {
			event.TraceId = uuid.New().String()
		}

		payload, err := json.Marshal(event)
		if err != nil {
			return
		}

		req, err := http.NewRequest("POST", s.CollectorURL, bytes.NewBuffer(payload))
		if err != nil {
			return
		}
		req.Header.Set("Content-Type", "application/json")
		resp, err := s.HTTPClient.Do(req)
		if err == nil && resp != nil {
			resp.Body.Close()
		}
	}()
}
