package main

import (
	"sync"
)

const MAX_ALLOWED_FILES_TO_BE_OPENED_IN_DEFAULT_PROGRAM = 32
const DEFAULT_PAGE_SIZE = 10
const TAG_CONTEXT_MAX_SIZE = 5

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
	Modified           CoreDateField                 `json:"modified"`
	Sorted             string                        `json:"sorted"`
	PageSize           int                           `json:"page-size"`
	Page               int                           `json:"page"`
	FilePaths          []string                      `json:"file-paths"`
	Ids                []int                         `json:"ids"`
	Tags               []string                      `json:"tags"`
	WithResponseFields WithCoreAppDataResponseFields `json:"response-fields"`
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

type TreeTag struct {
	Name     string              `json:"name"`
	Children map[string]*TreeTag `json:"children"`
}
type CloudItemWithContext struct {
	Item    string   `json:"item"`
	Context []string `json:"context"`
}
type WithCoreAppDataResponseFields struct {
	NodeSorting       bool `json:"nodes-sorted"`
	TotalNodes        bool `json:"total-nodes"`
	TotalNodesPages   bool `json:"total-nodes-pages"`
	CoreDirectory     bool `json:"core-directory"`
	DateNow           bool `json:"date-now"`
	Nodes             bool `json:"nodes"`
	CloudCanSelect    bool `json:"cloud-can-select"`
	Cloud             bool `json:"cloud"`
	CloudContext      bool `json:"cloud-context"`
	TreeTag           bool `json:"tree-tag"`
	CalendarCanSelect bool `json:"calendar-can-select"`
	Calendar          bool `json:"calendar"`
}
type CoreAppDataResponse struct {
	Error             string                 `json:"error"`
	NodeSorting       string                 `json:"nodes-sorted"`
	TotalNodes        uint32                 `json:"total-nodes"`
	TotalNodesPages   int                    `json:"total-nodes-pages"`
	CoreDirectory     string                 `json:"core-directory"`
	DateNow           CoreDateField          `json:"date-now"`
	Nodes             []*CoreNodeItem        `json:"nodes"`
	CloudCanSelect    map[string]bool        `json:"cloud-can-select"`
	Cloud             map[string]int         `json:"cloud"`
	CloudContext      []CloudItemWithContext `json:"cloud-context"`
	TreeTag           *TreeTag               `json:"tree-tag"`
	CalendarCanSelect CoreDateFacet          `json:"calendar-can-select"`
	Calendar          CoreDateFacet          `json:"calendar"`
}
