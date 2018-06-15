package main

import (
	"github.com/go-test/deep"
	"github.com/stretchr/testify/assert"
	"os"
	"path/filepath"
	"strings"
	"testing"
)

// Returns whether the assertion was successful (true) or not (false).
func MikEqual(t assert.TestingT, actual, expected interface{}, msgAndArgs ...interface{}) bool {
	if !assert.ObjectsAreEqual(expected, actual) {
		if diff := deep.Equal(expected, actual); diff != nil {
			assert.Fail(t, strings.Join(diff, "\n"), msgAndArgs...)
		}
	}
	return true
}

func WithDachaInDir(t *testing.T, root_dir string, cb func()) {
	cnis := newCoreNodeItemStorage("testing")
	cnis.MutableCreate(buildDachaDataset())
	assert.Equal(t, BuildProjectOnDataSet(root_dir, cnis.nodes), nil)
	cb()
	assert.Equal(t, TearDownProjectOnDataSet(root_dir), nil)
}
func WithSimpleInDir(t *testing.T, root_dir string, cb func()) {
	cnis := newCoreNodeItemStorage("testing")
	cnis.MutableCreate(buildDemoDataset())
	assert.Equal(t, BuildProjectOnDataSet(root_dir, cnis.nodes), nil)
	cb()
	assert.Equal(t, TearDownProjectOnDataSet(root_dir), nil)
}

func TearDownProjectOnDataSet(root_dir string) error {
	return os.RemoveAll(root_dir)
}

func TestGenerateNiceLookingTreeDataSet(t *testing.T) {
	cnis := newCoreNodeItemStorage("testing")
	cnis.MutableCreate(buildDachaDataset())
	tmp_dir := "dacha_set/"
	assert.Equal(t, BuildProjectOnDataSet(tmp_dir, cnis.nodes), nil)
	assert.Equal(t, TearDownProjectOnDataSet(tmp_dir), nil)
}

func TestBuildingAppStateOnFS(t *testing.T) {
	WithSimpleInDir(t, "simple_in_dir", func() {
		cnis := newCoreNodeItemStorage("testing")
		nodes, err := fs_backend.BuildAppStateOnAFolder("simple_in_dir")
		assert.Equal(t, err, nil)
		cnis.MutableCreate(nodes)
		cnis.GetAppData(*newCoreQuery()).MetaCloud()
		assert.Equal(t, cnis.GetAppData(*newCoreQuery()).SimpleCloud(),
			map[string]int{"work": 20, "bibliostore": 8, "translator": 2, "natan": 13, "wiki": 1, "everybook": 1,
				"amazon": 2, "согласовать": 1, "moscow_market": 9, "sforim": 2, "скачка_источников": 1, "биржа": 2,
				"магазины": 2, "UI": 1, "personal": 4, "blog": 1, "devops": 1, "zeldin": 2, "usecases": 2, "работа_сделана": 1})
		assert.Equal(t, len(cnis.nodes), 22)
	})
}

func TestBuildSymlinksInADefaultProgram(t *testing.T) {
	WithSimpleInDir(t, "simple_in_dir", func() {
		cnis := newCoreNodeItemStorage("testing")
		nodes, err := fs_backend.BuildAppStateOnAFolder("simple_in_dir")
		assert.Equal(t, err, nil)
		cnis.MutableCreate(nodes)
		fpathes := []string{}
		for _, v := range cnis.nodes {
			fpathes = append(fpathes, v.FilePath)
		}
		temp_dir, err := fs_backend.SymlinkInTempGivenPathes(fpathes)
		assert.Equal(t, err, nil)
		assert.Equal(t, len(fs_ls(temp_dir).Files), 22)
		assert.Equal(t, len(fs_backend.getTempDirsCreated()), 1)
		fs_backend.DropTempDirsCreated()

		// cnis.FSActionOnAListOfFiles(*newCoreQuery(), "symlinks")
	})
}
