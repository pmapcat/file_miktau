package main

import (
	"sync"
	"time"
)

const MAX_ALLOWED_FILES_TO_BE_OPENED_IN_DEFAULT_PROGRAM = 32
const DEFAULT_PAGE_SIZE = 10
const TAG_CONTEXT_MAX_SIZE = 5

type CoreNodeItemStorage struct {
	sync.RWMutex
	sorting_aggregator  ThesaurusAndSortingAggregator
	nodes               []*CoreNodeItem
	core_dir            string
	_transact_happening bool
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
	Sorted    string   `json:"sorted"`
	PageSize  int      `json:"page-size"`
	Page      int      `json:"page"`
	FilePaths []string `json:"file-paths"`
	Ids       []int    `json:"ids"`
	Tags      []string `json:"tags"`
}

type CoreNodeItem struct {
	Id                      int       `json:"id"`
	Name                    string    `json:"name"`
	FilePath                string    `json:"file-path"`
	MetaTags                []string  `json:"meta-tags"`
	Tags                    []string  `json:"tags"`
	FileSizeInMb            int       `json:"file-size-in-mb"`
	FileExtensionLowerCased string    `json:"file-extension-lower-cased"`
	Modified                time.Time `json:"modified"`
}

type CoreAppDataResponse struct {
	Error           string              `json:"error"`
	NodeSorting     string              `json:"nodes-sorted"`
	TotalNodes      uint32              `json:"total-nodes"`
	TotalNodesPages int                 `json:"total-nodes-pages"`
	CoreDirectory   string              `json:"core-directory"`
	Nodes           []*CoreNodeItem     `json:"nodes"`
	Patriarchs      []string            `json:"patriarchs"`
	CloudCanSelect  map[string]bool     `json:"cloud-can-select"`
	Cloud           map[string]int      `json:"cloud"`
	CloudContext    map[string][]string `json:"cloud-context"`
}

type Aggregator interface {
	Accumulate(n *CoreNodeItem)
	Aggregate(n *CoreNodeItem)
}
