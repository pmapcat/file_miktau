package main

import (
	"github.com/stretchr/testify/assert"
	"math/rand"
	"rand"
	"strings"
	"testing"
)

func makeTestableCoreNonDeterministicForUnderLoad() CoreNodeItemStorage {
	cnis := newCoreNodeItemStorage()
	for _, v := range buildDemoDataset() {
		fname, tags, _ := v[0], v[1], v[2]
		rdf := randomDateField()
		cnis.MutableAddNode(strings.Split(tags, " "), fname, rdf.Day, rdf.Month, rdf.Year)
	}
	return cnis
}

func makeTestableCore() CoreNodeItemStorage {
	cnis := newCoreNodeItemStorage()
	for _, v := range buildDemoDataset() {
		fname, tags, _ := v[0], v[1], v[2]
		rdf := randomDateField()
		cnis.MutableAddNode(strings.Split(tags, " "), fname, rdf.Day, rdf.Month, rdf.Year)
	}
	return cnis
}

func TestLoremGenerator(t *testing.T) {
	res := []string{}
	for _, v := range buildDemoDataset() {
		res = append(res, v[1])
	}
	mok := markovLorem(strings.Join(res, " "))
	assert.Equal(t, mok(12), "")
	assert.Equal(t, mok(0), "")
	assert.Equal(t, mok(4), "")
	assert.Equal(t, mok(200), "")
}
func TestGetAppData(t *testing.T) {

}
func TestGetAppDataUnderLoad(t *testing.T) {

}
