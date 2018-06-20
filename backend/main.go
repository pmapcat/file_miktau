package main

import (
	"flag"
)

const (
	MAX_ALLOWED_FILES_TO_BE_OPENED_IN_FILE_EXPLORER   = 100
	MAX_ALLOWED_FILES_TO_BE_OPENED_IN_DEFAULT_PROGRAM = 32
	DEFAULT_PAGE_SIZE                                 = 10
	TAG_CONTEXT_MAX_SIZE                              = 5
	DEFAULT_PERMISSION                                = 0777
	TEMP_DIR_PREFIX                                   = "metator_prefix_"
	PATCH_DB_PREFIX                                   = "metator_database_file.db"
	DEMO_DACHA_PATH                                   = "/dacha_data_set/"
	DEMO_DATA_PATH                                    = "/demo_data_set/"
	EMPTY_DATA_PATH                                   = ":empty:"
	PATCH_DB_BUCKET                                   = "metator_bucket"
	STRATEGY_SYMLINK                                  = 1
	STRATEGY_MOVE                                     = 2
	STRATEGY_COPY                                     = 3
	STRATEGY_DEFAULT_PROGRAM                          = 4
)

var CNIS = WrapSync(NewEmptyAppState())
var HOOKS_LIST = (func() []func(*AppStateItem) {
	return []func(*AppStateItem){FileSystemHooks}
})()

func main() {
	port := flag.Int("port", 4000, "On what port should the app be served")
	flag.Parse()
	fs_backend.DropTempDirsCreated()

	demo_data, err := NewAppStateOnFolder("test_data/", AppStateItemIdentity)
	FailOnError(err)
	CNIS.SwapAppState(demo_data)
	serve.Serve(*port)
}
