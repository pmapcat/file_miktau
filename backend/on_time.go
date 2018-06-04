package main

import (
	"time"
)

type on_time_ struct {
	should_mock_time bool
	mocked_time      time.Time
}

var on_time = on_time_{should_mock_time: false, mocked_time: time.Time{}}

func (o *on_time_) Now() time.Time {
	if o.should_mock_time {
		return o.mocked_time
	}
	return time.Now()
}

// return Date in UTC
func (o *on_time_) OnDate(year int, month int, day int) time.Time {
	return time.Date(year, time.Month(month), day, 0, 0, 0, 0, time.UTC)
}
func (o *on_time_) OnDateTime(year int, month int, day int, hour, minute int) time.Time {
	return time.Date(year, time.Month(month), day, hour, minute, 0, 0, time.UTC)
}

func (o *on_time_) GmailLikeFormat(date_time time.Time) string {
	now := o.Now()

	hours_since := now.Sub(date_time).Hours()
	// date time is in the future
	// good thing is to just throw an exception,
	// but, let it just be a default date.
	if hours_since < 0 {
		return date_time.Format("2006/01/02")
	}
	// this day
	if hours_since < 24 {
		return date_time.Format("15:04")
	}
	// this month
	if hours_since < 24*10 {
		return date_time.Format("Jan 02")
	}
	// this year
	return date_time.Format("2006/01/02")
}

func (o *on_time_) WithWhateverTime(mocked_time time.Time, cb func()) {
	o.should_mock_time = true
	o.mocked_time = mocked_time
	cb()
	o.should_mock_time = false
	o.mocked_time = time.Time{}
}
