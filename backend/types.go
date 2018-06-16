package main

import (
	"os"
	"sync"
)

const (
	MAX_ALLOWED_FILES_TO_BE_OPENED_IN_FILE_EXPLORER   = 100
	MAX_ALLOWED_FILES_TO_BE_OPENED_IN_DEFAULT_PROGRAM = 32
	DEFAULT_PAGE_SIZE                                 = 10
	TAG_CONTEXT_MAX_SIZE                              = 5
	DEFAULT_PERMISSION                                = 0777
	TEMP_DIR_PREFIX                                   = "metator_prefix_"
	PATCH_DB_PREFIX                                   = "metator_database_file.db"

	STRATEGY_SYMLINK         = 1
	STRATEGY_MOVE            = 2
	STRATEGY_COPY            = 3
	STRATEGY_DEFAULT_PROGRAM = 4
)

var PATCH_DB_BUCKET = []byte("metator_db_bucket")

type CoreNodeItemStorage struct {
	sync.RWMutex
	sorting_aggregator      ThesaurusAndSortingAggregator
	sorting_meta_aggregator MetaThesaurusAndSortingAggregator
	nodes                   []*CoreNodeItem
	core_dir                string
	_transact_happening     bool
}

type ModifyRecordsRequest struct {
	RecordsAffected int       `json:"records-affected"`
	Error           string    `json:"error"`
	TagsToAdd       []string  `json:"tags-to-add"`
	TagsToDelete    []string  `json:"tags-to-delete"`
	Request         CoreQuery `json:"request"`
}

type PushNewFilesRequest struct {
	Error error `json:"error"`
	// symlink || move || copy
	RootResolve  string   `json:"root-resolve"`
	NewFilePaths []string `json:"new-file-paths"`
	NewFileIds   []int    `json:"new-file-ids"`
}

type FileActionRequest struct {
	Error   error     `json:"error"`
	Action  int       `json:"action"` // symlinks/default/filebrowser
	Request CoreQuery `json:"request"`
}
type SwitchFoldersRequest struct {
	Error    error  `json:"error"`
	FilePath string `json:"file-path"`
}

type CoreQuery struct {
	Sorted                            string   `json:"sorted"`
	PageSize                          int      `json:"page-size"`
	Page                              int      `json:"page"`
	FilePaths                         []string `json:"file-paths"`
	Ids                               []int    `json:"ids"`
	BaseTags                          []string `json:"tags"`
	_getter_on_tags_is_already_called bool
	_standart_tags                    []string `json:"-"`
	_meta_tags                        []string `json:"-"`
}

type CoreNodeItem struct {
	Id                      int         `json:"id"`
	Name                    string      `json:"name"`
	FilePath                string      `json:"file-path"`
	FileInfo                os.FileInfo `json:"-"`
	MetaTags                []string    `json:"meta-tags"`
	Tags                    []string    `json:"tags"`
	FileSizeInMb            int         `json:"file-size-in-mb"`
	FileExtensionLowerCased string      `json:"file-extension-lower-cased"`
	Modified                JSONTime    `json:"modified"`
}

type CoreAppDataResponse struct {
	Error           string                    `json:"error"`
	NodeSorting     string                    `json:"nodes-sorted"`
	TotalNodes      int                       `json:"total-nodes"`
	TotalNodesPages int                       `json:"total-nodes-pages"`
	CoreDirectory   string                    `json:"core-directory"`
	Nodes           []*CoreNodeItem           `json:"nodes"`
	Patriarchs      []string                  `json:"patriarchs"`
	CloudCanSelect  map[string]bool           `json:"cloud-can-select"`
	Cloud           map[string]int            `json:"cloud"`
	CloudContext    map[string]map[string]int `json:"cloud-context"`
}

type OpenFileInDefaultProgramRequst struct {
	FilePath string `json:"file-path"`
	Error    error
}
