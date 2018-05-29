package main

import (
	"github.com/ant0ine/go-json-rest/rest"
	"log"
	"net/http"
	"strconv"
)

var CNIS = newCoreNodeItemStorage("empty")

type serve_ struct {
}

var serve = serve_{}

func (s *serve_) GetAppData(w rest.ResponseWriter, r *rest.Request) {
	// lock data. (Read lock, because getting doesn't require write access to the struct)
	CNIS.RLock()
	defer CNIS.RUnlock()
	enq := CoreQuery{}

	// response encode/decode error
	err := r.DecodeJsonPayload(&enq)
	if err != nil {
		w.WriteJson(newErrorCoreAppDataResponse(err))
		return
	}
	w.WriteJson(CNIS.GetAppData(enq))

	// response app data results

}

func (s *serve_) UpdateRecords(w rest.ResponseWriter, r *rest.Request) {
	// lock main structure
	// write lock (will require changing of the main structure)
	CNIS.Lock()
	defer CNIS.Unlock()

	// decode payload
	enq := ModifyRecordsRequest{}
	err := r.DecodeJsonPayload(&enq)
	if err != nil {
		w.WriteJson(newErrorModifyRecordsResponse(err))
		return
	}
	// process request
	enq.RecordsAffected = CNIS.MutableAddRemoveTagsToSelection(enq.Request, enq.TagsToAdd, enq.TagsToDelete)
	w.WriteJson(enq)
}

func (s *serve_) BulkFileWorkage(w rest.ResponseWriter, r *rest.Request) {
	// lock main structure
	// read lock (will not require changing of the main structure)
	CNIS.RLock()
	defer CNIS.RUnlock()
	enq := FileActionRequest{}
	err := r.DecodeJsonPayload(&enq)
	if err != nil {
		w.WriteJson(newErrorBulkFileWorkage(err))
		return
	}
	// process request & write response
	enq.Error = CNIS.FSActionOnAListOfFiles(enq.Request, enq.Action)
	w.WriteJson(enq)
}

func (s *serve_) SwitchFolders(w rest.ResponseWriter, r *rest.Request) {
	// lock main structure
	// write lock (will require "rewiping" of the working data structure)
	CNIS.Lock()
	defer CNIS.Unlock()

	enq := SwitchFoldersRequest{}
	err := r.DecodeJsonPayload(&enq)
	if err != nil {
		w.WriteJson(newErrorSwitchFoldersRequest(err))
		return
	}

	// build new system structure based on
	// newly loaded data from FS
	nodes, err := fs_backend.BuildAppStateOnAFolder(enq.FilePath)
	if err != nil {
		w.WriteJson(newErrorSwitchFoldersRequest(err))
		return
	}
	CNIS.RebirthWithNewData(nodes)
	w.WriteJson(enq)
}
func (s *serve_) CheckIsLive(w rest.ResponseWriter, r *rest.Request) {
	w.WriteJson(map[string]string{"status": "alive and kickin!"})
}

func (s *serve_) Serve(port int) {
	api := rest.NewApi()
	api.Use(rest.DefaultDevStack...)
	router, err := rest.MakeRouter(
		rest.Get("/api", s.CheckIsLive),
		rest.Post("/api/get-app-data", s.GetAppData),
		rest.Post("/api/update-records", s.UpdateRecords),
		rest.Post("/api/bulk-operate-on-files", s.BulkFileWorkage),
		rest.Post("/api/switch-projects", s.SwitchFolders),
	)
	if err != nil {
		log.Fatal(err)
	}
	api.SetApp(router)
	log.Fatal(http.ListenAndServe(":"+strconv.Itoa(port), api.MakeHandler()))
}
