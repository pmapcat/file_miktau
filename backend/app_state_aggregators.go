package main

import (
	"sort"
)

type ThesaurusAndSortingAggregator struct {
	thesaurus  map[string]int
	context    map[string]map[string]int
	patriarchs map[string]bool
}

func newThesaurusAndSortingAggregator() *ThesaurusAndSortingAggregator {
	r := ThesaurusAndSortingAggregator{}
	r.reinit()
	return &r
}

func (t *ThesaurusAndSortingAggregator) OnBeforeRun() {
	t.reinit()
}
func (t *ThesaurusAndSortingAggregator) reinit() {
	t.thesaurus = map[string]int{}
	t.context = map[string]map[string]int{}
	t.patriarchs = map[string]bool{}
}

func (t *ThesaurusAndSortingAggregator) GetThesaurus() map[string]int {
	return t.thesaurus
}
func (T *ThesaurusAndSortingAggregator) GetTagContext() map[string]map[string]int {
	return T.context
}
func (t *ThesaurusAndSortingAggregator) GetPatriarchs() []string {
	res := []string{}
	for k, _ := range t.patriarchs {
		res = append(res, k)
	}
	sort.Strings(res)
	return res
}

// context is limited to N <TAG_CONTEXT_MAX_SIZE>, default is 5.
// overall, if ~1000  tags expected, it is manageable amount.
// while TagTree is going to spiral out of control pretty soon,
// as TagTree size depends on interconnectivity, but not on the amount of tags,
// meaning ALL/ALL connections should give 1000^1000 amount of nodes, which is unmanageable
// although, ALL/ALL is incredibly rare (as in, all nodes are in all categories in the project)
// typical amount is 2-3 per node
// and 1000 categories per project is even more so,
// I don't want to risk it
// so I am replacing TagTree with TagContext. (Tag + 3/5 tags related to it)
// which is:
// * much mor manageable in terms of size (linear)
// * easier to reason about. (non recursive)

func (t *ThesaurusAndSortingAggregator) Accumulate(node *AppStateItem) {
	for _, tag := range node.Tags {
		// thesaurus accumulation
		t.thesaurus[tag] += 1
		// context accumulation
		item, ok := t.context[tag]
		if !ok {
			item = map[string]int{}
		}
		if len(item) < TAG_CONTEXT_MAX_SIZE {
			for _, btag := range node.Tags {
				if btag == tag {
					continue
				}

				if len(item) > TAG_CONTEXT_MAX_SIZE {
					break
				}
				item[btag] += 1
			}
			t.context[tag] = item
		}
	}
}

func (t *ThesaurusAndSortingAggregator) Aggregate(m *AppStateItem) {
	sort.Slice(m.Tags, func(i int, j int) bool {
		return t.thesaurus[m.Tags[i]] > t.thesaurus[m.Tags[j]]
	})

	// top level tags should be top level on patriarch
	if len(m.Tags) > 0 {
		t.patriarchs[m.Tags[0]] = true
	}
}

type MetaThesaurusAndSortingAggregator struct {
	thesaurus map[string]int
	context   map[string]map[string]int
}

func newMetaThesaurusAndSortingAggregator() *ThesaurusAndSortingAggregator {
	r := ThesaurusAndSortingAggregator{}
	r.reinit()
	return &r
}

func (t *MetaThesaurusAndSortingAggregator) OnBeforeRun() {
	t.reinit()
}
func (t *MetaThesaurusAndSortingAggregator) reinit() {
	t.thesaurus = map[string]int{}
	t.context = map[string]map[string]int{}
}

func (t *MetaThesaurusAndSortingAggregator) GetThesaurus() map[string]int {
	return t.thesaurus
}
func (T *MetaThesaurusAndSortingAggregator) GetTagContext() map[string]map[string]int {
	return T.context
}

func (t *MetaThesaurusAndSortingAggregator) Accumulate(node *AppStateItem) {
	for _, tag := range node.MetaTags {
		// thesaurus accumulation
		t.thesaurus[tag] += 1
		// context accumulation
		item, ok := t.context[tag]
		if !ok {
			item = map[string]int{}
		}
		if len(item) < TAG_CONTEXT_MAX_SIZE {
			for _, btag := range node.Tags {
				if btag == tag {
					continue
				}

				if len(item) > TAG_CONTEXT_MAX_SIZE {
					break
				}
				item[btag] += 1
			}
			t.context[tag] = item
		}
	}
}

func (t *MetaThesaurusAndSortingAggregator) Aggregate(m *AppStateItem) {
	sort.Slice(m.MetaTags, func(i int, j int) bool {
		return t.thesaurus[m.MetaTags[i]] > t.thesaurus[m.MetaTags[j]]
	})
}
