package main

func newErrorCoreAppDataResponse(err error) CoreAppDataResponse {
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
func newErrorSwitchFoldersRequest(err error) SwitchFoldersRequest {
	return SwitchFoldersRequest{Error: err}
}
