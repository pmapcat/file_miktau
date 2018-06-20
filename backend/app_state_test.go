package main

import (
	"fmt"
	"github.com/stretchr/testify/assert"
	"github.com/willf/bitset"
	"strings"
	"testing"
)

func TestPlayingWithBits(t *testing.T) {
	so := bitset.New(10)
	so.Set(580)
	so.Test(580)
	so.SetTo(12, true)
	// assert.Equal(t, so.DumpAsBits(), "b")
}

func TestLoremGenerator(t *testing.T) {
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
	res := NewEmptyAppState()
	timeEval(fmt.Sprintf("Inserting data in bulk on %v", perfsize), func() {
		res.MutableCreate(tstd)

	})
	timeEval(fmt.Sprintf("Baseline get_app_data on %v", perfsize), func() {
		NewAppStateResponse(res, *newQuery())
	})

	timeEval(fmt.Sprintf("With sorting get_app_data on %v", perfsize), func() {
		NewAppStateResponse(res, *newQuery().OrderBy("-modified"))
	})
	timeEval(fmt.Sprintf("With hot(cahe) sorting get_app_data on %v", perfsize), func() {
		NewAppStateResponse(res, *newQuery().OrderBy("-modified"))
	})

	timeEval(fmt.Sprintf("With faceting get_app_data on %v", perfsize), func() {
		assert.Equal(t, int(NewAppStateResponse(res, *newQuery().WithTags("natan", "магазины", "sforim")).TotalNodes), 13120)
	})
	timeEval(fmt.Sprintf("Just default app data gotten on %v", perfsize), func() {
		assert.Equal(t, int(NewAppStateResponse(res, *newQuery()).TotalNodes), 100001)
	})

	timeEval(fmt.Sprintf("Checking amount of nodes, because it should be less than %v %v", DEFAULT_PAGE_SIZE, perfsize), func() {
		assert.Equal(t, int(len(NewAppStateResponse(res, *newQuery().WithTags("natan", "магазины", "sforim")).Nodes)), DEFAULT_PAGE_SIZE)
	})

}
func TestAddingBug(t *testing.T) {
	cnis := NewAppStateFromDemoDataSet(1)
	// make none of them have any tags
	cnis.MutableUpdate(*newQuery(), func(item *AppStateItem) *AppStateItem {
		item.Tags = []string{}
		return item
	})
}

func TestApplyFilter(t *testing.T) {
	cnis := NewAppStateFromDemoDataSet(1)
	// check subset mechanics
	assert.Equal(t, cnis.nodes[1].ApplyFilter(newQuery().WithTags("natan")), true)
	assert.Equal(t, cnis.nodes[1].ApplyFilter(newQuery().WithTags("natan", "work")), true)
	assert.Equal(t, cnis.nodes[1].ApplyFilter(newQuery().WithTags("natan", "work", "ZAZAZAZA")), false)

	// check empty tag mechanics
	assert.Equal(t, cnis.nodes[0].ApplyFilter(newQuery()), true)
	assert.Equal(t, cnis.nodes[1].ApplyFilter(newQuery()), true)
	// check select by id mechanics
	assert.Equal(t, cnis.nodes[1].ApplyFilter(newQuery().WithIds(1)), true)
	assert.Equal(t, cnis.nodes[1].ApplyFilter(newQuery().WithIds(0, 2)), false)

}
func TestAllNodesShouldHaveDifferentId(t *testing.T) {
	cnis := NewAppStateFromDemoDataSet(1)
	assert.Equal(t, cnis.GetItemsSorted("name")[0].Id, 2)
	assert.Equal(t, cnis.GetItemsSorted("name")[1].Id, 0)
	assert.Equal(t, cnis.GetItemsSorted("name")[2].Id, 21)
	assert.Equal(t, cnis.GetItemsSorted("name")[3].Id, 4)
}
func TestAllNodesShouldHaveDifferentIdInSearchResponse(t *testing.T) {
	nodes := NewAppStateResponse(NewAppStateFromDemoDataSet(1), *newQuery()).Nodes
	assert.Equal(t, nodes[0].Id, 0)
	assert.Equal(t, nodes[1].Id, 1)
	assert.Equal(t, nodes[2].Id, 2)
	assert.Equal(t, nodes[3].Id, 3)
}

func TestCloudShouldntHaveEmptyTags(t *testing.T) {
	cnis := NewAppStateFromDemoDataSet(1)
	MikEqual(t,
		NewAppStateResponse(cnis, *newQuery()).SimpleCloud(),
		map[string]int{"work": 20, "bibliostore": 8, "translator": 2, "natan": 13, "wiki": 1, "everybook": 1, "amazon": 2, "согласовать": 1, "moscow_market": 9, "sforim": 2, "скачка_источников": 1, "биржа": 2, "магазины": 2, "UI": 1, "personal": 4, "blog": 1, "devops": 1, "zeldin": 2, "usecases": 2, "работа_сделана": 1})

	for tag_name, _ := range NewAppStateResponse(cnis, *newQuery()).SimpleCloud() {
		assert.NotEqual(t, tag_name, "")
		assert.NotEqual(t, strings.TrimSpace(tag_name), "")
	}

}

func TestCloudCouldSelectShouldntHaveEmptyTags(t *testing.T) {
	cnis := NewAppStateFromDemoDataSet(1)
	for tag_name, _ := range NewAppStateResponse(cnis, *newQuery()).CloudCanSelect {
		assert.NotEqual(t, tag_name, "")
		assert.NotEqual(t, strings.TrimSpace(tag_name), "")
	}
}

func TestGettingStationaryAppData(t *testing.T) {
	cnis := NewAppStateFromDemoDataSet(1)
	// test getting cloud
	MikEqual(t, NewAppStateResponse(cnis, *newQuery()).SimpleCloud(),
		map[string]int{"translator": 2, "магазины": 2, "personal": 4, "blog": 1, "usecases": 2, "amazon": 2, "биржа": 2, "moscow_market": 9, "devops": 1, "wiki": 1, "согласовать": 1, "UI": 1, "zeldin": 2, "natan": 13, "work": 20, "bibliostore": 8, "sforim": 2, "скачка_источников": 1, "everybook": 1, "работа_сделана": 1})

	assert.Equal(t, NewAppStateResponse(cnis, *newQuery().WithTags("natan")).SimpleCloudCanSelect(),
		map[string]bool{"natan": true, "work": true, "translator": true, "amazon": true, "devops": true, "биржа": true, "UI": true, "moscow_market": true, "согласовать": true, "bibliostore": true, "wiki": true, "sforim": true, "скачка_источников": true, "магазины": true})

	assert.Equal(t, NewAppStateResponse(cnis, *newQuery()).SimpleCloudCanSelect(),
		map[string]bool{"bibliostore": true, "wiki": true, "биржа": true, "personal": true, "work": true, "moscow_market": true, "translator": true, "sforim": true, "скачка_источников": true, "работа_сделана": true, "магазины": true, "UI": true, "blog": true, "zeldin": true, "natan": true, "amazon": true, "devops": true, "согласовать": true, "usecases": true, "everybook": true})

	// test sorted results
	assert.Equal(t, NewAppStateResponse(cnis, *newQuery().OrderBy("modified")).Nodes[0].Modified.Time().String(), "2018-07-19 00:00:00 +0000 UTC")
	assert.Equal(t, NewAppStateResponse(cnis, *newQuery().OrderBy("-modified")).Nodes[0].Modified.Time().String(), "2016-02-05 00:00:00 +0000 UTC")

	assert.Equal(t, NewAppStateResponse(cnis, *newQuery().OrderBy("name")).Nodes[0].Name, "blab.mp4")
	assert.Equal(t, NewAppStateResponse(cnis, *newQuery().OrderBy("-name")).Nodes[0].Name, "zlop.mp4")

}
func TestAggregation(t *testing.T) {
	cnis := NewAppStateFromDemoDataSet(1)
	assert.Equal(t, NewAppStateResponse(cnis, *newQuery()).Patriarchs, []string{"work", "работа_сделана"})
	assert.Equal(t, NewAppStateResponse(cnis, *newQuery()).TotalNodes, 22)
	assert.Equal(t, len(NewAppStateResponse(cnis, *newQuery()).CloudContext), len(NewAppStateResponse(cnis, *newQuery()).Cloud))

	assert.Equal(t, NewAppStateResponse(cnis, *newQuery()).SimpleCloudContext()["work"], map[string]int{"natan": 7, "bibliostore": 6, "moscow_market": 6, "translator": 1, "amazon": 1})
	assert.Equal(t, NewAppStateResponse(cnis, *newQuery()).SimpleCloudContext()["translator"],
		map[string]int{"amazon": 1, "natan": 2, "work": 2, "bibliostore": 2, "devops": 1, "moscow_market": 1})
}

func TestMutableWorkage(t *testing.T) {
	cnis := NewAppStateFromDemoDataSet(1)
	cnis.MutableAddRemoveTagsToSelection(*newQuery().WithTags("work"), []string{"bolo"}, []string{"work"})
	assert.Equal(t, NewAppStateResponse(cnis, *newQuery()).SimpleCloud(), map[string]int{"devops": 1, "translator": 2, "wiki": 1,
		"sforim": 2, "согласовать": 1, "personal": 4, "blog": 1, "zeldin": 2, "everybook": 1,
		"natan": 13, "bolo": 20, "скачка_источников": 1, "биржа": 2, "UI": 1, "usecases": 2,
		"moscow_market": 9, "amazon": 2, "магазины": 2, "bibliostore": 8, "работа_сделана": 1})
}

// trying to replicate *exact* error conditions
func TestFixingEmptyFieldsForDataSet(t *testing.T) {
	assert.Equal(t, NewAppStateResponse(NewAppStateFromDemoDataSet(1), *newQuery()).SimpleCloud(), map[string]int{"devops": 1, "wiki": 1, "zeldin": 2, "work": 20, "sforim": 2,
		"скачка_источников": 1, "биржа": 2, "UI": 1, "personal": 4, "bibliostore": 8,
		"amazon": 2, "blog": 1, "usecases": 2, "natan": 13, "moscow_market": 9,
		"translator": 2, "согласовать": 1, "магазины": 2, "everybook": 1, "работа_сделана": 1})

}
func TestGetAppData(t *testing.T) {
	cnis := NewAppStateFromDemoDataSet(1)
	// typical query (only tags)
	assert.Equal(t, len(NewAppStateResponse(cnis, *newQuery().WithTags("natan").WithPage(2)).Nodes), 3)
	// typical query (empty request)
	assert.Equal(t, len(NewAppStateResponse(cnis, *newQuery().WithPage(3)).Nodes), 2)
}

func TestGettingNodesByFilePaths(t *testing.T) {
	cnis := NewAppStateFromDemoDataSet(1)
	counter := 0
	cnis.GetInBulk(*newQuery().WithFilePathes("/home/mik/chosen_one/",
		"/home/mik/this_must_be_it/blab.mp4",
		"/home/mik/this_must_be_it/hello.mp4",
		"/home/mik/this_must_be_it/blab.mp4"), func(cni *AppStateItem) {
		counter += 1
	})
	assert.Equal(t, counter, 3)
}

func TestMetaGetAppData(t *testing.T) {
	on_time.WithWhateverTime(on_time.OnDate(2003, 02, 17), func() {
		cnis := NewAppStateFromDemoDataSet(1)
		assert.Equal(t, NewAppStateResponse(cnis, *newQuery()).MetaCloud(),
			map[string]int{"@file-type:video": 22, "@modified-month:7": 4, "@modified-year:2018": 2, "@empty:is-empty?": 1, "@modified-year:2017": 6, "@modified-month:3": 1, "@file-size:10—50mb": 5, "@file-size:100—500mb": 4, "@file-size:1—10mb": 12, "@modified-year:2016": 14, "@modified-month:4": 4, "@file-size:50—100mb": 1, "@modified:in-future": 22, "@modified-month:2": 8, "@modified-month:1": 1, "@modified-month:5": 4})

		assert.Equal(t, len(NewAppStateResponse(cnis, *newQuery().WithTags("@modified-year:2018")).MetaCloudCanSelect()), 6)
		assert.Equal(t, len(NewAppStateResponse(cnis, *newQuery().WithTags("@modified-year:2016")).MetaCloudCanSelect()), 13)
		assert.Equal(t, len(NewAppStateResponse(cnis, *newQuery().WithTags("@modified-year:2017")).MetaCloudCanSelect()), 10)
	})

	cnis := NewAppStateFromDemoDataSet(1)
	assert.Equal(t, NewAppStateResponse(cnis, *newQuery()).MetaCloudContext()["@file-type:video"],
		map[string]int{"natan": 4, "work": 4, "bibliostore": 4, "moscow_market": 3, "translator": 1})

	assert.Equal(t, NewAppStateResponse(cnis, *newQuery()).MetaCloudContext()["@file-type:video"],
		map[string]int{"natan": 4, "work": 4, "bibliostore": 4, "moscow_market": 3, "translator": 1})

	on_time.WithWhateverTime(on_time.OnDate(2016, 8, 02), func() {
		cnis := NewAppStateFromDemoDataSet(1)
		assert.Equal(t, NewAppStateResponse(cnis, *newQuery()).MetaCloud(),
			map[string]int{"@modified:this-month": 2, "@file-size:50—100mb": 1, "@empty:is-empty?": 1, "@modified-month:5": 4, "@file-size:100—500mb": 4, "@modified:in-future": 8, "@file-size:1—10mb": 12, "@modified-month:3": 1, "@file-size:10—50mb": 5, "@modified-month:7": 4, "@modified-year:2017": 6, "@modified-year:2018": 2, "@modified-year:2016": 14, "@modified:this-year": 12, "@modified-month:2": 8, "@modified-month:1": 1, "@file-type:video": 22, "@modified-month:4": 4})

	})
}

func TestDachaBaselineDataset(t *testing.T) {
	cnis := NewAppStateFromDachaDataSet(1)
	MikEqual(t, NewAppStateResponse(cnis, *newQuery().WithTags("дервья")).SimpleCloud(),
		map[string]int{"дом": 23, "ближний": 2, "утепление": 2, "обратный_клапан": 1, "магистраль_до_колодца": 1, "потребные_материалы": 2, "сад": 3, "окна": 2, "верстак": 1, "лестница": 1, "план_чередования": 1, "конструкция": 2, "чертежи": 1, "ворот": 1, "раковина": 2, "ванная": 2, "душ": 1, "на_кухню": 1, "к_душу": 2, "огород": 4, "стол": 1, "от_раковины_в_ванной": 1, "от_раковины_в_туалете": 1, "от_раковины_с": 1, "дача": 37, "канализация": 2, "уличная_мебель": 1, "вывод_из_дома": 1, "сушилка": 1, "отопление": 1, "шланг_верхний": 1, "тросы_подвеса_насоса": 1, "газовый_балон": 1, "каркас": 1, "магистраль_в": 1, "колодец": 2, "фундамент": 2, "санузел": 4, "план_грядок": 2, "однолетники": 1, "каркас_под_насос": 1, "фильтрационный_колодец": 1, "погреб": 4, "перед_домом": 1, "у_дорожки_слева": 2, "шланг_нижний": 1, "к_раковине_в_туалете": 1, "цвет?": 1, "стелажи": 2, "многолетники": 1, "сорта_культур": 1, "к_ванне": 1, "перекрытия": 1, "обшивка": 1, "у_дорожки_справа": 2, "уплотнение": 2, "эскизы": 1, "стулья": 1, "туалет": 1, "кухни": 1, "припасы": 2, "беседка": 3, "стены": 3, "вода": 2, "доме": 1, "крыша": 2, "замки": 2, "тумбы": 1, "замок": 1, "гриль": 1, "дверь": 2, "внутренние": 1, "трос_ведра": 1, "разводка_воды_в_доме": 3, "к_раковине_в_ванной": 1, "холодильник": 1, "сарай": 3, "перегородки": 1, "деревья": 1, "кусты": 1, "клубника": 1, "ручки": 3, "насос": 1, "полки": 1, "плита": 1, "ванна": 1, "пол": 1, "забор": 1, "малина": 1, "наружные": 8, "ведро": 1, "ввод": 1, "двери": 9, "кухня": 3, "цветники": 4, "перед_беседкой": 1, "дальний": 2, "список_культур": 2, "к_унитазу": 1, "освещение": 1})

	assert.Equal(t, NewAppStateResponse(cnis, *newQuery().WithTags("деревья")).SimpleCloudCanSelect(),
		map[string]bool{"дача": true, "сад": true, "деревья": true, "кусты": true, "цветники": true})
}
