package main

import (
	"github.com/ant0ine/go-json-rest/rest"
	"log"
	"net/http"
	"strconv"
)

var CNIS = newCoreNodeItemStorage()

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
	// response app data results
	w.WriteJson(CNIS.GetAppData(enq))
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
	defer CNIS.Unlock()
	// ==================== > CONTINUE FROM HERE < ==========================
}

func (s *serve_) SwitchFolders(w rest.ResponseWriter, r *rest.Request) {

}

func (s *serve_) Serve(port int) {
	api := rest.NewApi()
	api.Use(rest.DefaultDevStack...)
	router, err := rest.MakeRouter(
		rest.Post("/get-app-data", s.GetAppData),
		rest.Post("/update-records", s.UpdateRecords),
		rest.Post("/bulk-operate-on-files", s.BulkOperateOnFiles),
		rest.Post("/switch-projects", s.SwitchFolders),
	)
	if err != nil {
		log.Fatal(err)
	}
	api.SetApp(router)
	log.Fatal(http.ListenAndServe(":"+strconv.Itoa(port), api.MakeHandler()))
}
