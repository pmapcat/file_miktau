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

	STRATEGY_SYMLINK         = 1
	STRATEGY_MOVE            = 2
	STRATEGY_COPY            = 3
	STRATEGY_DEFAULT_PROGRAM = 4
)

func main() {
	port := flag.Int("port", 4000, "On what port should the app be served")
	flag.Parse()

	fs_backend.DropTempDirsCreated()

	// LogErr("Cannot build simple_set", BuildProjectOnDataSet("./test_data/simple_set/", buildDemoDataset()))
	// LogErr("Cannot build dacha_set", BuildProjectOnDataSet("./test_data/dacha_set/", buildDachaDataset()))

	// demo_data, _ := fs_backend.BuildEmptyAppState("")
	// demo_data, _ := fs_backend.BuildAppStateWithNoUserTags("")

	demo_data, _ := NewAppStateOnFolderIdentity("./test_data/simple_set/")
	CNIS.__mutableCreate(demo_data)
	demo_dacha, _ := NewAppStateOnFolderIdentity("./test_data/dacha_set/")
	CNIS.__mutableCreate(demo_dacha)
	serve.Serve(*port)
}
