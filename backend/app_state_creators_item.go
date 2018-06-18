package main

import (
	"os"
	fp "path/filepath"
	"strings"
)

func newAppStateItemFromStat(filepath string, stat os.FileInfo) *AppStateItem {
	return &AppStateItem{
		Id:                      -1,
		Name:                    stat.Name(),
		FilePath:                filepath,
		MetaTags:                []string{},
		Tags:                    []string{},
		FileSizeInMb:            int(stat.Size() / 1048576),
		FileExtensionLowerCased: strings.ToLower(fp.Ext(stat.Name())),
		Modified:                newJSONTime(stat.ModTime()),
	}
}
