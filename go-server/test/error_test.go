package main

import "testing"

func TestTimeConsuming(t *testing.T) {
	actualResult := "Hello"
	var expectedResult = "Hello"

	if actualResult != expectedResult {
		t.Fatalf("Expected %s but got %s", expectedResult, actualResult)
	}
}
