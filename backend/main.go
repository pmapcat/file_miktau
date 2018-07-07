package main

import (
	"flag"
)

const (
	// not iota, bc needed for external API
	STRATEGY_SYMLINK         = "symlinks"
	STRATEGY_DEFAULT_PROGRAM = "default"
	STRATEGY_MOVE            = 1
	STRATEGY_COPY            = 2
)

const (
	MAX_ALLOWED_FILES_TO_BE_OPENED_IN_FILE_EXPLORER   = 153
	MAX_ALLOWED_FILES_TO_BE_OPENED_IN_DEFAULT_PROGRAM = 32
	DEFAULT_PAGE_SIZE                                 = 10
	TAG_CONTEXT_MAX_SIZE                              = 5
	DEFAULT_PERMISSION                                = 0777
	TEMP_DIR_PREFIX                                   = "metator_prefix_"
	PATCH_DB_PREFIX                                   = "metator_database_file.db"
	EMPTY_DATA_PATH                                   = ":empty:"
	PATCH_DB_BUCKET                                   = "metator_bucket"
)

var USE_PATCH_DB = false
var CNIS = WrapSync(NewEmptyAppState())

func main() {
	port := flag.Int("port", 4000, "On what port should the app be served")
	flag.Parse()

	// removing stale folders from previous runs
	fs_backend.DropTempDirsCreated()

	// load demo data set
	LogErr("loading demo data set:", CNIS.MutableSwitchFolders("/home/mik/Downloads/metator_experiments/"))

	CNIS.AppState().AddSubscription(newFileSystemAggregator())

	// make :stub:, empty app state. Now, the user should select
	// working folder
	serve.Serve(*port)
}
