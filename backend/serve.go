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

type RestNodeItem struct {
	Modified string   `json:"modified"`
	Tags     []string `json:"tags"`
	Name     string   `json:"name"`
}
type RestCalendar struct {
	Year  []string `json:"year"`
	Month []string `json:"month"`
	Day   []string `json:"day"`
}
type RestCloudItem struct {
	Name string         `json:"string"`
	Tags map[string]int `json:"tags"`
}

type RestNodeSorted struct {
	Field     string `json:"field"`
	IsReverse bool   `json:"reverse?"`
}

type RestAppDataResponse struct {
	NodeSorting       RestNodeSorted  `json:"nodes-sorted"`
	Nodes             []RestNodeItem  `json:"nodes"`
	CloudCanSelect    []string        `json:"cloud-can-select"`
	Cloud             []RestCloudItem `json:"cloud"`
	CalendarCanSelect RestCalendar    `json:"calendar-can-select"`
	Calendar          RestCalendar    `json:"calendar"`
}

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
