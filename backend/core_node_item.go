package main

import (
	fp "path/filepath"
	"strconv"
	"strings"
	"time"
)

func (n *CoreNodeItem) ModifiedInDays() uint32 {
	return uint32(n.Modified.Unix() / 86400)
}

func newCoreNodeItemFromDemoDataSet(fsize, fpath, tags, fname string, date time.Time) *CoreNodeItem {
	fsize_in_mb, err := strconv.Atoi(fsize)
	if err != nil {
		panic(err)
	}
	return &CoreNodeItem{
		Id:                      -1,
		Name:                    strings.TrimSpace(fname),
		FilePath:                fpath,
		MetaTags:                []string{},
		Tags:                    strings.Split(strings.TrimSpace(tags), " "),
		FileSizeInMb:            fsize_in_mb,
		FileExtensionLowerCased: strings.ToLower(fp.Ext(fname)),
		Modified:                date,
	}
}

// will have to call only on mutable actions
func (n *CoreNodeItem) ApplyFilter(c *CoreQuery) bool {
	if result, whether_applicable := apply_filter.FilterByFilePaths(n, c); whether_applicable {
		return result
	}
	if result, whether_applicable := apply_filter.FilterByIds(n, c); whether_applicable {
		return result
	}
	if result, whether_applicable := apply_filter.MatchEmpty(n, c); whether_applicable {
		return result
	}

	if result, whether_applicable := apply_filter.IsSubSet(n.Tags, c.Tags); whether_applicable {
		return result
	}
	if result, whether_applicable := apply_filter.IsSubSet(n.MetaTags, c.MetaTags); whether_applicable {
		return result
	}
	return true
}

func (n *CoreNodeItem) TagRoot(thesaurus map[string]int) string {
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

func (n *CoreNodeItem) AddTags(tags []string) {
	n.Tags = append(n.Tags, tags...)
}
func (n *CoreNodeItem) RemoveTags(tags []string) {
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

func (n *CoreNodeItem) IsTagged() bool {
	return len(n.Tags) > 0
}
