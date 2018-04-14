package main

import (
	"fmt"
	"github.com/stretchr/testify/assert"
	"log"
	"strings"
	"testing"
)

func TestLoremGenerator(t *testing.T) {
	log.SetFlags(log.LstdFlags | log.Lshortfile)
	res := []string{}
	for _, v := range buildDemoDataset() {
		res = append(res, v.Tags...)
	}

	mok := markovLorem(strings.Join(res, " "))
	assert.Equal(t, mok(12),
		"translator amazon natan work bibliostore translator amazon natan work bibliostore translator amazon natan")
	assert.Equal(t, mok(0), "")
	assert.Equal(t, mok(4), "usecases work personal usecases work")
	assert.Equal(t, len(strings.Split(mok(200), " ")), 200)
}
func TestPerformance(t *testing.T) {
	perfsize := 100000
	tstd := generateStressDataSet(buildDemoDataset(), perfsize)
	res := newCoreNodeItemStorage()
	timeEval(fmt.Sprintf("Inserting data in bulk on %v", perfsize), func() {
		res.MutableAddManyForTestPurposes(tstd)
	})
	timeEval(fmt.Sprintf("Baseline get_app_data on %v", perfsize), func() {
		res.GetAppData(*newCoreQuery())
	})
	timeEval(fmt.Sprintf("With sorting get_app_data on %v", perfsize), func() {
		res.GetAppData(*newCoreQuery().OrderBy("-modified"))
	})
	timeEval(fmt.Sprintf("With hot(cahe) sorting get_app_data on %v", perfsize), func() {
		res.GetAppData(*newCoreQuery().OrderBy("-modified"))
	})

	timeEval(fmt.Sprintf("With faceting get_app_data on %v", perfsize), func() {
		assert.Equal(t, int(res.GetAppData(*newCoreQuery().WithTags("natan", "магазины", "sforim")).TotalNodes), 13234)
	})

}

func TestApplyFilter(t *testing.T) {
	cnis := newCoreNodeItemStorage()
	cnis.MutableAddManyForTestPurposes(buildDemoDataset())
	// check subset mechanics
	assert.Equal(t, cnis.nodes[1].ApplyFilter(newCoreQuery().WithTags("natan")), true)
	assert.Equal(t, cnis.nodes[1].ApplyFilter(newCoreQuery().WithTags("natan", "work")), true)
	assert.Equal(t, cnis.nodes[1].ApplyFilter(newCoreQuery().WithTags("natan", "work", "ZAZAZAZA")), false)

	// check empty tag mechanics
	assert.Equal(t, cnis.nodes[0].ApplyFilter(newCoreQuery()), true)
	assert.Equal(t, cnis.nodes[1].ApplyFilter(newCoreQuery()), true)
	// check select by id mechanics
	assert.Equal(t, cnis.nodes[1].ApplyFilter(newCoreQuery().WithIds(1)), true)
	assert.Equal(t, cnis.nodes[1].ApplyFilter(newCoreQuery().WithIds(0, 2)), false)

	// check choose by date mechanics
	assert.Equal(t, cnis.nodes[1].ApplyFilter(newCoreQuery().WithDate(2017, 0, 0)), true)
	assert.Equal(t, cnis.nodes[1].ApplyFilter(newCoreQuery().WithDate(2016, 0, 0)), false)
	assert.Equal(t, cnis.nodes[1].ApplyFilter(newCoreQuery().WithDate(0, 7, 0)), true)
	assert.Equal(t, cnis.nodes[1].ApplyFilter(newCoreQuery().WithDate(0, 8, 0)), false)
	assert.Equal(t, cnis.nodes[1].ApplyFilter(newCoreQuery().WithDate(0, 0, 20)), true)
	assert.Equal(t, cnis.nodes[1].ApplyFilter(newCoreQuery().WithDate(0, 0, 21)), false)

	// choose by date combined mechanics
	assert.Equal(t, cnis.nodes[1].ApplyFilter(newCoreQuery().WithDate(2017, 7, 20)), true)
	assert.Equal(t, cnis.nodes[1].ApplyFilter(newCoreQuery().WithDate(2016, 7, 20)), false)

}
func TestGettingStationaryAppData(t *testing.T) {
	cnis := newCoreNodeItemStorage()
	cnis.MutableAddManyForTestPurposes(buildDemoDataset())
	// test getting cloud
	assert.Equal(t, cnis.GetAppData(*newCoreQuery()).Cloud,
		map[string]map[string]int{"": map[string]int{},
			"work": map[string]int{"natan": 13, "bibliostore": 8, "blog": 1, "moscow_market": 9, "devops": 1, "wiki": 1, "zeldin": 2, "translator": 2, "amazon": 2, "sforim": 2, "UI": 1, "usecases": 2, "work": 20, "согласовать": 1, "скачка_источников": 1, "биржа": 2, "магазины": 2, "personal": 4, "everybook": 1},
			"работа_сделана": map[string]int{"работа_сделана": 1}})

	assert.Equal(t, cnis.GetAppData(*newCoreQuery()).Calendar, CoreDateFacet{
		Year:  map[int]int{2018: 2, 2016: 14, 2017: 6},
		Month: map[int]int{5: 4, 3: 1, 1: 1, 7: 4, 2: 8, 4: 4},
		Day:   map[int]int{4: 1, 3: 1, 14: 1, 12: 1, 10: 1, 8: 1, 5: 2, 18: 1, 13: 1, 11: 1, 2: 1, 24: 1, 21: 1, 19: 1, 17: 1, 16: 1, 9: 1, 20: 1, 15: 1, 7: 1, 1: 1}})

	assert.Equal(t, cnis.GetAppData(*newCoreQuery().WithTags("zeldin")).CalendarCanSelect, CoreDateFacet{
		Year:  map[int]int{2016: 2},
		Month: map[int]int{5: 2},
		Day:   map[int]int{3: 1, 2: 1}})
	assert.Equal(t, cnis.GetAppData(*newCoreQuery()).CalendarCanSelect, CoreDateFacet{
		Year:  map[int]int{2018: 2, 2016: 14, 2017: 6},
		Month: map[int]int{5: 4, 3: 1, 1: 1, 7: 4, 2: 8, 4: 4},
		Day:   map[int]int{4: 1, 3: 1, 14: 1, 12: 1, 10: 1, 8: 1, 5: 2, 18: 1, 13: 1, 11: 1, 2: 1, 24: 1, 21: 1, 19: 1, 17: 1, 16: 1, 9: 1, 20: 1, 15: 1, 7: 1, 1: 1}})

	assert.Equal(t, cnis.GetAppData(*newCoreQuery().WithTags("natan")).CloudCanSelect,
		map[string]bool{"natan": true, "work": true, "translator": true, "amazon": true, "devops": true, "биржа": true, "UI": true, "moscow_market": true, "согласовать": true, "bibliostore": true, "wiki": true, "sforim": true, "скачка_источников": true, "магазины": true})

	assert.Equal(t, cnis.GetAppData(*newCoreQuery()).CloudCanSelect,
		map[string]bool{"bibliostore": true, "wiki": true, "биржа": true, "personal": true, "work": true, "moscow_market": true, "translator": true, "sforim": true, "скачка_источников": true, "работа_сделана": true, "магазины": true, "UI": true, "blog": true, "zeldin": true, "natan": true, "amazon": true, "devops": true, "согласовать": true, "usecases": true, "everybook": true})

	// test sorted results
	assert.Equal(t, cnis.GetAppData(*newCoreQuery().OrderBy("modified")).Nodes[0].Modified, CoreDateField{Year: 2018, Month: 7, Day: 19})
	assert.Equal(t, cnis.GetAppData(*newCoreQuery().OrderBy("-modified")).Nodes[0].Modified, CoreDateField{Year: 2016, Month: 2, Day: 5})

	assert.Equal(t, cnis.GetAppData(*newCoreQuery().OrderBy("name")).Nodes[0].Name, "blab.mp4")
	assert.Equal(t, cnis.GetAppData(*newCoreQuery().OrderBy("-name")).Nodes[0].Name, "zlop.mp4")

}
func TestGetAppData(t *testing.T) {
	log.SetFlags(log.LstdFlags | log.Lshortfile)
	cnis := newCoreNodeItemStorage()
	cnis.MutableAddManyForTestPurposes(buildDemoDataset())

	// assert.Equal(t, cnis.GetAppData(*newCoreQuery()).Cloud, "blab")

	// typical query (mixed)
	assert.Equal(t, len(cnis.GetAppData(*newCoreQuery().WithTags("natan", "магазины", "sforim").WithDate(2017, 04, 10).OrderBy("-modified")).Nodes), 1)

	// typical query (only tags)
	assert.Equal(t, len(cnis.GetAppData(*newCoreQuery().WithTags("natan")).Nodes), 13)
	// typical query (only date)
	assert.Equal(t, len(cnis.GetAppData(*newCoreQuery().WithDate(2016, 2, 5)).Nodes), 1)
	// typical query (empty request)
	assert.Equal(t, len(cnis.GetAppData(*newCoreQuery()).Nodes), 22)

}
