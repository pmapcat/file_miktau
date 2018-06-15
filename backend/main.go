package main

import (
	"flag"
)

func main() {
	port := flag.Int("port", 4000, "On what port should the app be served")
	flag.Parse()

	fs_backend.DropTempDirsCreated()

	// LogErr("Cannot build simple_set", BuildProjectOnDataSet("./test_data/simple_set/", buildDemoDataset()))
	// LogErr("Cannot build dacha_set", BuildProjectOnDataSet("./test_data/dacha_set/", buildDachaDataset()))

	// demo_data, _ := fs_backend.BuildEmptyAppState("")
	// demo_data, _ := fs_backend.BuildAppStateWithNoUserTags("")

	demo_data, _ := fs_backend.BuildAppStateOnAFolder("./test_data/simple_set/")
	CNIS.MutableCreate(demo_data)
	demo_dacha, _ := fs_backend.BuildAppStateOnAFolder("./test_data/dacha_set/")
	CNIS.MutableCreate(demo_dacha)
	serve.Serve(*port)
}
