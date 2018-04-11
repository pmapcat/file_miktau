package main

type NodeItem struct {
	Tags     []string
	Modified struct {
		Year  int
		Month int
		Day   int
	}
}

type another_core_ struct {
	nodes []*NodeItem
}

// Must have operations:
// GetDrillableCloudBySpace(tags: "year:2018 hello world",space = "day:",space=":")
// GetCloud()
