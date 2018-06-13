package main

import (
	"flag"
)

func main() {
	port := flag.Int("port", 4000, "On what port should the app be served")

	// demo_data, _ := fs_backend.BuildEmptyAppState("")
	// demo_data, _ := fs_backend.BuildAppStateWithNoUserTags("")
	demo_data, _ := fs_backend.BuildAppStateOnAFolder("/home/mik/some.demo.project/")
	CNIS.MutableCreate(demo_data)
	// // add dacha dataset
	reso := []*CoreNodeItem{}
	for i := 0; i <= 1; i++ {
		reso = append(reso, buildDachaDataset()...)
	}
	CNIS.MutableCreate(reso)

	serve.Serve(*port)
}
