package main

import (
	"github.com/ant0ine/go-json-rest/rest"
	"log"
	"net/http"
	"strconv"
)

type serve_ struct {
}

var serve = serve_{}

func (s *serve_) GetAppData(w rest.ResponseWriter, r *rest.Request) {
	rsp := RestAppDataResponse{}

}

func (s *serve_) Serve(port int) {
	api := rest.NewApi()
	api.Use(rest.DefaultDevStack...)
	router, err := rest.MakeRouter(
		rest.Get("/get-app-data", s.GetAppData),
	)
	if err != nil {
		log.Fatal(err)
	}
	api.SetApp(router)
	log.Fatal(http.ListenAndServe(":"+strconv.Itoa(port), api.MakeHandler()))
}
