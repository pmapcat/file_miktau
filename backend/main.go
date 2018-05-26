package main

import ()

func main() {
	demo_data, _ := fs_backend.BuildAppStateOnAFolder("/home/mik/some.demo.project/")
	CNIS.RebirthWithNewData(demo_data)
	CNIS.MutableAddMany(buildDachaDataset())
	for i := 0; i <= 1000; i++ {
		CNIS.MutableAddMany(buildDachaDataset())
	}
	serve.Serve(4000)
}
