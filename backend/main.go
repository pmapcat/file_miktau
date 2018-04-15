package main

import ()

func main() {
	demo_data, _ := fs_backend.BuildAppStateOnAFolder("/home/mik/some.demo.project/")
	CNIS.RebirthWithNewData(demo_data)
	serve.Serve(8000)
}
