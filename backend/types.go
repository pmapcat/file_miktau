package main

import (
	"sync"
)

const MAX_ALLOWED_FILES_TO_BE_OPENED_IN_DEFAULT_PROGRAM = 32

type CoreNodeItemStorage struct {
	sync.RWMutex
	nodes    []*CoreNodeItem
	core_dir string
}

type ModifyRecordsRequest struct {
	RecordsAffected int       `json:"records-affected"`
	Error           string    `json:"error"`
	TagsToAdd       []string  `json:"tags-to-add"`
	TagsToDelete    []string  `json:"tags-to-delete"`
	Request         CoreQuery `json:"request"`
}
type FileActionRequest struct {
	Error   error     `json:"error"`
	Action  string    `json:"action"` // symlinks/default/filebrowser
	Request CoreQuery `json:"request"`
}
type SwitchFoldersRequest struct {
	Error    error  `json:"error"`
	FilePath string `json:"file-path"`
}

type CoreQuery struct {
	Modified  CoreDateField `json:"modified"`
	Sorted    string        `json:"sorted"`
	FilePaths []string      `json:"file-paths"`
	Ids       []int         `json:"ids"`
	Tags      []string      `json:"tags"`
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
	FilePath       string        `json:"file-path"`
	Tags           []string      `json:"tags"`
	Modified       CoreDateField `json:"modified"`
	_modified_days uint32
}

type CoreAppDataResponse struct {
	Error             string                    `json:"error"`
	NodeSorting       string                    `json:"nodes-sorted"`
	TotalNodes        uint32                    `json:"total-nodes"`
	CoreDirectory     string                    `json:"core-directory"`
	Nodes             []*CoreNodeItem           `json:"nodes"`
	CloudCanSelect    map[string]bool           `json:"cloud-can-select"`
	Cloud             map[string]map[string]int `json:"cloud"`
	CalendarCanSelect CoreDateFacet             `json:"calendar-can-select"`
	Calendar          CoreDateFacet             `json:"calendar"`
}
