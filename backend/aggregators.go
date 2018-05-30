package main

import (
	"sort"
)

type ThesaurusAndSortingAggregator struct {
	thesaurus  map[string]int
	context    map[string][]string
	patriarchs map[string]bool
}

func newThesaurusAndSortingAggregator() *ThesaurusAndSortingAggregator {
	return &ThesaurusAndSortingAggregator{
		thesaurus:  map[string]int{},
		context:    map[string][]string{},
		patriarchs: map[string]bool{},
	}
}

func (t *ThesaurusAndSortingAggregator) GetThesaurus() map[string]int {
	return t.thesaurus
}
func (T *ThesaurusAndSortingAggregator) GetTagContext() map[string][]string {
	return T.context
}
func (t *ThesaurusAndSortingAggregator) GetPatriarchs() []string {
	res := []string{}
	for k, _ := range t.patriarchs {
		res = append(res, k)
	}
	return res
}

// context is limited to N <TAG_CONTEXT_MAX_SIZE>, default is 5.
// overall, if ~1000  tags expected, it is manageable amount.
// while TagTree is going to spiral out of control pretty soon,
// as TagTree size depends on interconnectivity, but not on the amount of tags
func (t *ThesaurusAndSortingAggregator) Accumulate(node *CoreNodeItem) {
	for _, tag := range node.Tags {
		// thesaurus accumulation
		t.thesaurus[tag] += 1
		// context accumulation
		item, ok := t.context[tag]
		if !ok {
			item = []string{}
		}
		if len(item) < TAG_CONTEXT_MAX_SIZE {
			for _, btag := range node.Tags {
				if len(item) > TAG_CONTEXT_MAX_SIZE {
					break
				}
				item = append(item, btag)
			}
			t.context[tag] = item
		}
	}
}

func (t *ThesaurusAndSortingAggregator) Aggregate(m *CoreNodeItem) {
	sort.Slice(m.Tags, func(i int, j int) bool {
		return t.thesaurus[m.Tags[i]] > t.thesaurus[m.Tags[j]]
	})

	// top level tags should be top level on patriarch
	if len(m.Tags) > 0 {
		t.patriarchs[m.Tags[0]] = true
	}
}
