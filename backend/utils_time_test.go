package main

import (
	"github.com/stretchr/testify/assert"
	"testing"
)

func TestGmailLikeTime(t *testing.T) {
	on_time.WithWhateverTime(on_time.OnDate(2018, 12, 12), func() {
		// within a single day
		assert.Equal(t, on_time.GmailLikeFormatDeprecated(on_time.OnDateTime(2018, 12, 11, 23, 12)), "23:12")
		// within ten days
		assert.Equal(t, on_time.GmailLikeFormatDeprecated(on_time.OnDateTime(2018, 12, 9, 12, 34)), "Dec 09")
		// later
		assert.Equal(t, on_time.GmailLikeFormatDeprecated(on_time.OnDateTime(2017, 1, 9, 12, 34)), "2017/01/09")
	})

}
func TestWhateverTime(t *testing.T) {
	// check workage
	on_time.WithWhateverTime(on_time.OnDate(1992, 12, 12), func() {
		assert.Equal(t, on_time.Now().Year(), 1992)
		assert.Equal(t, int(on_time.Now().Month()), 12)
		assert.Equal(t, on_time.Now().Day(), 12)
	})
	// check, if was reset successfully
	assert.NotEqual(t, on_time.Now().Year(), 1992)
	// check another
	on_time.WithWhateverTime(on_time.OnDate(2018, 03, 02), func() {
		assert.Equal(t, on_time.Now().Year(), 2018)
		assert.Equal(t, int(on_time.Now().Month()), 03)
		assert.Equal(t, on_time.Now().Day(), 02)
	})
}
