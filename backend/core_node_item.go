package main

import (
	"sort"
)

func (n *CoreNodeItem) ModifiedInDays() uint32 {
	if n._modified_days > 0 {
		return n._modified_days
	}
	n._modified_days = uint32(n.Modified.Day + n.Modified.Month*30 + n.Modified.Year*365)
	return n._modified_days
}

// will have to call only on mutable actions
func (n *CoreNodeItem) ApplyFilter(c *CoreQuery) bool {
	if result, whether_applicable := apply_filter.FilterByFilePaths(n, c); whether_applicable {
		return result
	}
	if result, whether_applicable := apply_filter.FilterByIds(n, c); whether_applicable {
		return result
	}
	if result, whether_applicable := apply_filter.FilterByModifiedDate(n, c); whether_applicable {
		return result
	}
	if result, whether_applicable := apply_filter.MatchEmpty(n, c); whether_applicable {
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

func (m *CoreNodeItem) MostProminentDrill(the_thesaurus map[string]int) []string {
	sort.Slice(m.Tags, func(i int, j int) bool {
		return the_thesaurus[m.Tags[i]] > the_thesaurus[m.Tags[j]]
	})
	return m.Tags
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
