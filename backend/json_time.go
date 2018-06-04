package main

import (
	"time"
)

type JSONTime time.Time

func newJSONTime(t time.Time) JSONTime {
	return JSONTime(t)
}

func (t JSONTime) MarshalJSON() ([]byte, error) {
	return []byte(`"` + on_time.GmailLikeFormat(time.Time(t)) + `"`), nil
}
func (t JSONTime) Time() time.Time {
	return time.Time(t)
}
