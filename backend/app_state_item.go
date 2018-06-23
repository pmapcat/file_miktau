package main

import (
	"os"
	fp "path/filepath"
	"strconv"
	"strings"
	"time"
)

type AppStateItem struct {
	Id                      int         `json:"id"`
	Name                    string      `json:"name"`
	FilePath                string      `json:"file-path"`
	FileInfo                os.FileInfo `json:"-"`
	MetaTags                []string    `json:"meta-tags"`
	Tags                    []string    `json:"tags"`
	FileSizeInMb            int         `json:"file-size-in-mb"`
	FileExtensionLowerCased string      `json:"file-extension-lower-cased"`
	Modified                JSONTime    `json:"modified"`
}

func newAppStateItemFromFile(root string, stats os.FileInfo, fpath string) *AppStateItem {
	// tags are unrooted
	tags := strings.Split(strings.TrimPrefix(fp.Dir(fpath), root), string(fp.Separator))

	return &AppStateItem{
		Id:                      -1,
		Name:                    fp.Base(fpath),
		FilePath:                fpath,
		MetaTags:                []string{},
		Tags:                    tags,
		FileSizeInMb:            int(stats.Size() / 1000000),
		FileExtensionLowerCased: strings.ToLower(fp.Ext(stats.Name())),
		Modified:                newJSONTime(stats.ModTime()),
	}
}

func newAppStateItemFromDemoDataSet(fsize, fpath, tags, fname string, date time.Time) *AppStateItem {
	fsize_in_mb, err := strconv.Atoi(fsize)
	if err != nil {
		panic(err)
	}
	return &AppStateItem{
		Id:                      -1,
		Name:                    strings.TrimSpace(fname),
		FilePath:                fpath,
		MetaTags:                []string{},
		Tags:                    strings.Split(strings.TrimSpace(tags), " "),
		FileSizeInMb:            fsize_in_mb,
		FileExtensionLowerCased: strings.ToLower(fp.Ext(fname)),
		Modified:                newJSONTime(date),
	}

}

// will have to call only on mutable actions
func (n *AppStateItem) ApplyFilter(c *Query) bool {
	if result, whether_applicable := query_filter.FilterByFilePaths(n, c); whether_applicable {
		return result
	}
	if result, whether_applicable := query_filter.FilterByIds(n, c); whether_applicable {
		return result
	}

	if result, whether_applicable := query_filter.IsSubSet(n.Tags, c.GetTags()); whether_applicable {
		return result
	}
	if result, whether_applicable := query_filter.IsSubSet(n.MetaTags, c.GetMetaTags()); whether_applicable {
		return result
	}
	return true
}

func (n *AppStateItem) TagRoot(thesaurus map[string]int) string {
	max := 0
	max_tag := ""
	for _, tag := range n.Tags {
		if thesaurus[tag] > max {
			max_tag = tag
			max = thesaurus[tag]
		}
	}
	return max_tag
}

func (n *AppStateItem) AddTags(tags []string) {
	n.Tags = append(n.Tags, tags...)
}
func (n *AppStateItem) RemoveTags(tags []string) {
	result := []string{}
	for _, current_tag := range n.Tags {
		should_append := true
		for _, tag_to_remove := range tags {
			if current_tag == tag_to_remove {
				should_append = false
				break
			}
		}
		if should_append {
			result = append(result, current_tag)
		}
	}
	n.Tags = result
}

func (n *AppStateItem) IsTagged() bool {
	return len(n.Tags) > 0
}
