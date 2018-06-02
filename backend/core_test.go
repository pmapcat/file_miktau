package main

import (
	"fmt"
	"github.com/stretchr/testify/assert"
	"log"
	"strings"
	"testing"
	"time"
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
	res := newCoreNodeItemStorage("testing")
	timeEval(fmt.Sprintf("Inserting data in bulk on %v", perfsize), func() {
		res.MutableCreate(tstd)

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
		assert.Equal(t, int(res.GetAppData(*newCoreQuery().WithTags("natan", "магазины", "sforim")).TotalNodes), 13120)
	})
	timeEval(fmt.Sprintf("Just default app data gotten on %v", perfsize), func() {
		assert.Equal(t, int(res.GetAppData(*newCoreQuery()).TotalNodes), 100001)
	})

	timeEval(fmt.Sprintf("Checking amount of nodes, because it should be less than %v %v", DEFAULT_PAGE_SIZE, perfsize), func() {
		assert.Equal(t, int(len(res.GetAppData(*newCoreQuery().WithTags("natan", "магазины", "sforim")).Nodes)), DEFAULT_PAGE_SIZE)
	})

}
func TestAddingBug(t *testing.T) {
	cnis := newCoreNodeItemStorage("testing")
	cnis.MutableCreate(buildDemoDataset())

	// make none of them have any tags
	cnis.MutableUpdate(*newCoreQuery(), func(item *CoreNodeItem) *CoreNodeItem {
		item.Tags = []string{}
		return item
	})
}

func TestApplyFilter(t *testing.T) {
	cnis := newCoreNodeItemStorage("testing")
	cnis.MutableCreate(buildDemoDataset())
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

	// // check choose by date mechanics
	// assert.Equal(t, cnis.nodes[1].ApplyFilter(newCoreQuery().WithDate(2017, 0, 0)), true)
	// assert.Equal(t, cnis.nodes[1].ApplyFilter(newCoreQuery().WithDate(2016, 0, 0)), false)
	// assert.Equal(t, cnis.nodes[1].ApplyFilter(newCoreQuery().WithDate(0, 7, 0)), true)
	// assert.Equal(t, cnis.nodes[1].ApplyFilter(newCoreQuery().WithDate(0, 8, 0)), false)
	// assert.Equal(t, cnis.nodes[1].ApplyFilter(newCoreQuery().WithDate(0, 0, 20)), true)
	// assert.Equal(t, cnis.nodes[1].ApplyFilter(newCoreQuery().WithDate(0, 0, 21)), false)

	// choose by date combined mechanics
	// assert.Equal(t, cnis.nodes[1].ApplyFilter(newCoreQueryWithFullResponse().WithDate(2017, 7, 20)), true)
	// assert.Equal(t, cnis.nodes[1].ApplyFilter(newCoreQueryWithFullResponse().WithDate(2016, 7, 20)), false)
	// choose by file path mechanics

}
func TestAllNodesShouldHaveDifferentId(t *testing.T) {
	cnis := newCoreNodeItemStorage("testing")
	cnis.MutableCreate(buildDemoDataset())
	assert.Equal(t, cnis.GetNodesSorted("name")[0].Id, 2)
	assert.Equal(t, cnis.GetNodesSorted("name")[1].Id, 0)
	assert.Equal(t, cnis.GetNodesSorted("name")[2].Id, 21)
	assert.Equal(t, cnis.GetNodesSorted("name")[3].Id, 4)
}
func TestAllNodesShouldHaveDifferentIdInSearchResponse(t *testing.T) {
	cnis := newCoreNodeItemStorage("testing")
	cnis.MutableCreate(buildDemoDataset())
	nodes := cnis.GetAppData(*newCoreQuery()).Nodes
	assert.Equal(t, nodes[0].Id, 0)
	assert.Equal(t, nodes[1].Id, 1)
	assert.Equal(t, nodes[2].Id, 2)
	assert.Equal(t, nodes[3].Id, 3)
}

func TestCloudShouldntHaveEmptyTags(t *testing.T) {
	cnis := newCoreNodeItemStorage("testing")
	cnis.MutableCreate(buildDemoDataset())
	assert.Equal(t, cnis.GetAppData(*newCoreQuery()).Cloud, map[string]int{"work": 20, "bibliostore": 8, "translator": 2, "natan": 13, "wiki": 1, "everybook": 1, "amazon": 2, "согласовать": 1, "moscow_market": 9, "sforim": 2, "скачка_источников": 1, "биржа": 2, "магазины": 2, "UI": 1, "personal": 4, "blog": 1, "devops": 1, "zeldin": 2, "usecases": 2, "работа_сделана": 1})

	for tag_name, _ := range cnis.GetAppData(*newCoreQuery()).Cloud {
		assert.NotEqual(t, tag_name, "")
		assert.NotEqual(t, strings.TrimSpace(tag_name), "")
	}

}

func TestCloudCouldSelectShouldntHaveEmptyTags(t *testing.T) {
	cnis := newCoreNodeItemStorage("testing")
	cnis.MutableCreate(buildDemoDataset())
	for tag_name, _ := range cnis.GetAppData(*newCoreQuery()).CloudCanSelect {
		assert.NotEqual(t, tag_name, "")
		assert.NotEqual(t, strings.TrimSpace(tag_name), "")
	}
}

func TestGettingStationaryAppData(t *testing.T) {
	cnis := newCoreNodeItemStorage("testing")
	cnis.MutableCreate(buildDemoDataset())
	// test getting cloud
	assert.Equal(t, cnis.GetAppData(*newCoreQuery()).Cloud,
		map[string]int{"translator": 2, "магазины": 2, "personal": 4, "blog": 1, "usecases": 2, "amazon": 2, "биржа": 2, "moscow_market": 9, "devops": 1, "wiki": 1, "согласовать": 1, "UI": 1, "zeldin": 2, "natan": 13, "work": 20, "bibliostore": 8, "sforim": 2, "скачка_источников": 1, "everybook": 1, "работа_сделана": 1})

	assert.Equal(t, cnis.GetAppData(*newCoreQuery().WithTags("natan")).CloudCanSelect,
		map[string]bool{"natan": true, "work": true, "translator": true, "amazon": true, "devops": true, "биржа": true, "UI": true, "moscow_market": true, "согласовать": true, "bibliostore": true, "wiki": true, "sforim": true, "скачка_источников": true, "магазины": true})

	assert.Equal(t, cnis.GetAppData(*newCoreQuery()).CloudCanSelect,
		map[string]bool{"bibliostore": true, "wiki": true, "биржа": true, "personal": true, "work": true, "moscow_market": true, "translator": true, "sforim": true, "скачка_источников": true, "работа_сделана": true, "магазины": true, "UI": true, "blog": true, "zeldin": true, "natan": true, "amazon": true, "devops": true, "согласовать": true, "usecases": true, "everybook": true})

	// test sorted results
	assert.Equal(t, cnis.GetAppData(*newCoreQuery().OrderBy("modified")).Nodes[0].Modified.String(), "2018-07-19 00:00:00 +0000 UTC")
	assert.Equal(t, cnis.GetAppData(*newCoreQuery().OrderBy("-modified")).Nodes[0].Modified.String(), "2016-02-05 00:00:00 +0000 UTC")

	assert.Equal(t, cnis.GetAppData(*newCoreQuery().OrderBy("name")).Nodes[0].Name, "blab.mp4")
	assert.Equal(t, cnis.GetAppData(*newCoreQuery().OrderBy("-name")).Nodes[0].Name, "zlop.mp4")

}
func TestAggregation(t *testing.T) {
	cnis := newCoreNodeItemStorage("testing")
	cnis.MutableCreate(buildDemoDataset())
	assert.Equal(t, cnis.GetAppData(*newCoreQuery()).Patriarchs, []string{"work", "работа_сделана"})
	assert.Equal(t, cnis.GetAppData(*newCoreQuery()).TotalNodes, 22)
	assert.Equal(t, len(cnis.GetAppData(*newCoreQuery()).CloudContext), len(cnis.GetAppData(*newCoreQuery()).Cloud))

	assert.Equal(t, cnis.GetAppData(*newCoreQuery()).CloudContext["work"], map[string]int{"natan": 7, "bibliostore": 6, "moscow_market": 6, "translator": 1, "amazon": 1})
	assert.Equal(t, cnis.GetAppData(*newCoreQuery()).CloudContext["translator"],
		map[string]int{"amazon": 1, "natan": 2, "work": 2, "bibliostore": 2, "devops": 1, "moscow_market": 1})
}

func TestMutableWorkage(t *testing.T) {
	cnis := newCoreNodeItemStorage("testing")
	cnis.MutableCreate(buildDemoDataset())
	cnis.MutableAddRemoveTagsToSelection(*newCoreQuery().WithTags("work"), []string{"bolo"}, []string{"work"})
	assert.Equal(t, cnis.GetAppData(*newCoreQuery()).Cloud, map[string]int{"devops": 1, "translator": 2, "wiki": 1,
		"sforim": 2, "согласовать": 1, "personal": 4, "blog": 1, "zeldin": 2, "everybook": 1,
		"natan": 13, "bolo": 20, "скачка_источников": 1, "биржа": 2, "UI": 1, "usecases": 2,
		"moscow_market": 9, "amazon": 2, "магазины": 2, "bibliostore": 8, "работа_сделана": 1})
}

// trying to replicate *exact* error conditions
func TestFixingEmptyFieldsForDataSet(t *testing.T) {
	storage := newCoreNodeItemStorage("empty")
	demo_data, _ := fs_backend.BuildAppStateOnAFolder("/home/mik/some.demo.project/")
	storage.MutableRebirthWithNewData(demo_data)
	assert.Equal(t, storage.GetAppData(*newCoreQuery()).Cloud, map[string]int{"devops": 1, "wiki": 1, "zeldin": 2, "work": 20, "sforim": 2,
		"скачка_источников": 1, "биржа": 2, "UI": 1, "personal": 4, "bibliostore": 8,
		"amazon": 2, "blog": 1, "usecases": 2, "natan": 13, "moscow_market": 9,
		"translator": 2, "согласовать": 1, "магазины": 2, "everybook": 1, "работа_сделана": 1})

}
func TestGetAppData(t *testing.T) {
	log.SetFlags(log.LstdFlags | log.Lshortfile)
	cnis := newCoreNodeItemStorage("testing")
	cnis.MutableCreate(buildDemoDataset())

	// assert.Equal(t, cnis.GetAppData(*newCoreQueryWithFullResponse()).Cloud, "blab")

	// typical query (mixed)

	// typical query (only tags)
	assert.Equal(t, len(cnis.GetAppData(*newCoreQuery().WithTags("natan").WithPage(2)).Nodes), 3)
	// typical query (empty request)
	assert.Equal(t, len(cnis.GetAppData(*newCoreQuery().WithPage(3)).Nodes), 2)
}

func TestGettingNodesByFilePaths(t *testing.T) {
	cnis := newCoreNodeItemStorage("testing")
	cnis.MutableCreate(buildDemoDataset())

	counter := 0
	cnis.GetInBulk(*newCoreQuery().WithFilePathes("/home/mik/chosen_one/",
		"/home/mik/this_must_be_it/blab.mp4",
		"/home/mik/this_must_be_it/hello.mp4",
		"/home/mik/this_must_be_it/blab.mp4"), func(cni *CoreNodeItem) {
		counter += 1
	})
	assert.Equal(t, counter, 3)
}
func TestMetaGetAppData(t *testing.T) {

	cnis := newCoreNodeItemStorage("testing")
	cnis.MutableCreate(buildDemoDataset())
	assert.Equal(t, cnis.GetAppData(*newCoreQuery()).MetaCloud, "blab")
	assert.Equal(t, cnis.GetAppData(*newCoreQuery()).MetaCloudContext, "blab")
}

func TestDachaBaselineDataset(t *testing.T) {
	log.SetFlags(log.LstdFlags | log.Lshortfile)
	cnis := newCoreNodeItemStorage("testing")
	cnis.MutableCreate(buildDachaDataset())
	assert.Equal(t, cnis.GetAppData(*newCoreQuery().WithTags("дервья")).Cloud,
		map[string]int{"дом": 23, "ближний": 2, "утепление": 2, "обратный_клапан": 1, "магистраль_до_колодца": 1, "потребные_материалы": 2, "сад": 3, "окна": 2, "верстак": 1, "лестница": 1, "план_чередования": 1, "конструкция": 2, "чертежи": 1, "ворот": 1, "раковина": 2, "ванная": 2, "душ": 1, "на_кухню": 1, "к_душу": 2, "огород": 4, "стол": 1, "от_раковины_в_ванной": 1, "от_раковины_в_туалете": 1, "от_раковины_с": 1, "дача": 37, "канализация": 2, "уличная_мебель": 1, "вывод_из_дома": 1, "сушилка": 1, "отопление": 1, "шланг_верхний": 1, "тросы_подвеса_насоса": 1, "газовый_балон": 1, "каркас": 1, "магистраль_в": 1, "колодец": 2, "фундамент": 2, "санузел": 4, "план_грядок": 2, "однолетники": 1, "каркас_под_насос": 1, "фильтрационный_колодец": 1, "погреб": 4, "перед_домом": 1, "у_дорожки_слева": 2, "шланг_нижний": 1, "к_раковине_в_туалете": 1, "цвет?": 1, "стелажи": 2, "многолетники": 1, "сорта_культур": 1, "к_ванне": 1, "перекрытия": 1, "обшивка": 1, "у_дорожки_справа": 2, "уплотнение": 2, "эскизы": 1, "стулья": 1, "туалет": 1, "кухни": 1, "припасы": 2, "беседка": 3, "стены": 3, "вода": 2, "доме": 1, "крыша": 2, "замки": 2, "тумбы": 1, "замок": 1, "гриль": 1, "дверь": 2, "внутренние": 1, "трос_ведра": 1, "разводка_воды_в_доме": 3, "к_раковине_в_ванной": 1, "холодильник": 1, "сарай": 3, "перегородки": 1, "деревья": 1, "кусты": 1, "клубника": 1, "ручки": 3, "насос": 1, "полки": 1, "плита": 1, "ванна": 1, "пол": 1, "забор": 1, "малина": 1, "наружные": 8, "": 1, "ведро": 1, "ввод": 1, "двери": 9, "кухня": 3, "цветники": 4, "перед_беседкой": 1, "дальний": 2, "список_культур": 2, "к_унитазу": 1, "освещение": 1})

	assert.Equal(t, cnis.GetAppData(*newCoreQuery().WithTags("деревья")).CloudCanSelect,
		map[string]bool{"дача": true, "сад": true, "деревья": true, "кусты": true, "цветники": true})
}
