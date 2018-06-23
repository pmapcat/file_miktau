package main

import (
	"github.com/ant0ine/go-json-rest/rest"
	log "github.com/sirupsen/logrus"
	"net/http"
	"strconv"
)

type serve_ struct {
}

var serve = serve_{}

func (s *serve_) GetAppData(w rest.ResponseWriter, r *rest.Request) {
	CNIS.RLock()
	defer CNIS.RUnlock()
	enq := Query{}

	// response encode/decode error
	err := r.DecodeJsonPayload(&enq)
	if err != nil {
		w.WriteJson(newErrorCoreAppDataResponse(err))
		return
	}
	w.WriteJson(NewAppStateResponse(CNIS.AppState(), enq))
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
	enq.RecordsAffected = CNIS.AppState().MutableAddRemoveTagsToSelection(enq.Request, enq.TagsToAdd, enq.TagsToDelete)
	w.WriteJson(enq)
}

func (s *serve_) PushNewFiles(w rest.ResponseWriter, r *rest.Request) {
	// lock main structure
	// read lock (will not require changing of the main structure)
	CNIS.RLock()
	defer CNIS.RUnlock()
	enq := PushNewFilesRequest{}
	err := r.DecodeJsonPayload(&enq)
	if err != nil {
		w.WriteJson(newErrorPushNewFilesRequest(err))
		return
	}
	// process request & write response
	enq.NewFileIds, enq.Error = CNIS.AppState().SideEffectMutablePushNewFiles(enq.NewFilePaths)
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
	enq.Error = CNIS.AppState().SideEffectFileSystemActionOnAListOfFiles(enq.Request, enq.Action)
	w.WriteJson(enq)
}

func (s *serve_) OpenFileInADefaultProgram(w rest.ResponseWriter, r *rest.Request) {
	// lock main structure
	// read lock (will not require changing of the main structure)
	CNIS.RLock()
	defer CNIS.RUnlock()
	enq := OpenFileInDefaultProgramRequst{}
	err := r.DecodeJsonPayload(&enq)
	if err != nil {
		w.WriteJson(newErrorOpenFileInDefaultProgram(err))
		return
	}
	enq.Error = OpenFile(enq.FilePath)
	w.WriteJson(enq)
}

func (s *serve_) SwitchFolders(w rest.ResponseWriter, r *rest.Request) {
	// lock main structure
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
	err = CNIS.MutableSwitchFolders(enq.FilePath)
	if err != nil {
		w.WriteJson(newErrorSwitchFoldersRequest(err))
		return
	}
	w.WriteJson(enq)
}

func (s *serve_) CheckIsLive(w rest.ResponseWriter, r *rest.Request) {
	w.WriteJson(map[string]string{"status": "alive"})
}

func (s *serve_) Serve(port int) {
	api := rest.NewApi()

	if is_dev_environ() {
		api.Use(rest.DefaultProdStack...)
	} else {
		api.Use(rest.DefaultDevStack...)
	}

	router, err := rest.MakeRouter(
		rest.Get("/api", s.CheckIsLive),
		rest.Post("/api/get-app-data", s.GetAppData),
		rest.Post("/api/update-records", s.UpdateRecords),
		rest.Post("/api/bulk-operate-on-files", s.BulkFileWorkage),
		rest.Post("/api/switch-projects", s.SwitchFolders),
		rest.Post("/api/push-new-files", s.PushNewFiles),
		rest.Post("/api/open-file-in-default-program", s.OpenFileInADefaultProgram),
	)
	if err != nil {
		log.Fatal(err)
	}
	api.SetApp(router)
	http.Handle("/api/", api.MakeHandler())
	http.Handle("/", http.FileServer(http.Dir("public/")))
	log.Fatal(http.ListenAndServe(":"+strconv.Itoa(port), nil))

}
