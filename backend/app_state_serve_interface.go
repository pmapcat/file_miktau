package main

type ModifyRecordsRequest struct {
	RecordsAffected int      `json:"records-affected"`
	Error           string   `json:"error"`
	TagsToAdd       []string `json:"tags-to-add"`
	TagsToDelete    []string `json:"tags-to-delete"`
	Request         Query    `json:"request"`
}

type PushNewFilesRequest struct {
	Error error `json:"error"`
	// symlink || move || copy
	RootResolve  string   `json:"root-resolve"`
	NewFilePaths []string `json:"new-file-paths"`
	NewFileIds   []int    `json:"new-file-ids"`
}

type FileActionRequest struct {
	Error   error `json:"error"`
	Action  int   `json:"action"` // symlinks/default/filebrowser
	Request Query `json:"request"`
}
type SwitchFoldersRequest struct {
	Error    error  `json:"error"`
	FilePath string `json:"file-path"`
}

type OpenFileInDefaultProgramRequst struct {
	FilePath string `json:"file-path"`
	Error    error
}

func newErrorCoreAppDataResponse(err error) AppStateResponse {
	return CoreAppDataResponse{Error: err.Error()}
}
func newErrorModifyRecordsResponse(err error) ModifyRecordsRequest {
	return ModifyRecordsRequest{Error: err.Error()}
}

func newErrorPushNewFilesRequest(err error) PushNewFilesRequest {
	return PushNewFilesRequest{Error: err}
}

func newErrorBulkFileWorkage(err error) FileActionRequest {
	return FileActionRequest{Error: err}
}

func newErrorOpenFileInDefaultProgram(err error) OpenFileInDefaultProgramRequst {
	return OpenFileInDefaultProgramRequst{Error: err}
}

func newErrorSwitchFoldersRequest(err error) SwitchFoldersRequest {
	return SwitchFoldersRequest{Error: err}
}
