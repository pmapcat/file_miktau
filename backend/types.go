package main

type CoreNodeItemStorage struct {
	nodes         []*CoreNodeItem
	_thesaurus    map[string]int
	_nodes_sorted map[string][]*CoreNodeItem
}

type CoreQuery struct {
	Modified CoreDateField `json:"modified"`
	Sorted   string        `json:"sorted"`
	Ids      []int         `json:"ids"`
	Tags     []string      `json:"tags"`
}

type CoreDateField struct {
	Year  int `json:"year"`
	Month int `json:"month"`
	Day   int `json:"day"`
}
type CoreDateFacet struct {
	Year  map[int]int `json:"year"`
	Month map[int]int `json:"month"`
	Day   map[int]int `json:"day"`
}

type CoreNodeItem struct {
	Id             int           `json:"id"`
	Name           string        `json:"name"`
	Tags           []string      `json:"tags"`
	Modified       CoreDateField `json:"modified"`
	_modified_days uint32
}

type CoreAppDataResponse struct {
	NodeSorting       string                    `json:"nodes-sorted"`
	TotalNodes        uint32                    `json:"total-nodes"`
	Nodes             []*CoreNodeItem           `json:"nodes"`
	CloudCanSelect    map[string]bool           `json:"cloud-can-select"`
	Cloud             map[string]map[string]int `json:"cloud"`
	CalendarCanSelect CoreDateFacet             `json:"calendar-can-select"`
	Calendar          CoreDateFacet             `json:"calendar"`
}
