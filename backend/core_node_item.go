package main
import (
	"os"
)

func (n *CoreNodeItem) ModifiedInDays() uint32 {
	return uint32(n.Modified.Unix() / 86400)
}

func newCoreNodeItemFromStat(filepath string, stat os.FileInfo) *CoreNodeItem{
	stat.ModTime()
	
	stat.Size()
	stat.IsDir()
	
	return &CoreNodeItem{
		Id: -1,
		Name: stat.Name(),
		FilePath: filepath,
		MetaTags: []string{},
		Tags: tags,
		FileSizeInMb: 
		
		
	}	Name                    string    `json:"name"`
	FilePath                string    `json:"file-path"`
	MetaTags                []string  `json:"meta-tags"`
	Tags                    []string  `json:"tags"`
	FileSizeInMb            int       `json:"file-size-in-mb"`
	FileExtensionLowerCased string    `json:"file-extension-lower-cased"`
	Modified                time.Time `json:"modified"`

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
	if result, whether_applicable := apply_filter.IsMetaSubSet(n, c); whether_applicable {
		return result
	}
	if result, whether_applicable := apply_filter.IsSubSet(n, c); whether_applicable {
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
