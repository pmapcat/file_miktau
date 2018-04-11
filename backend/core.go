package main

// How does this work?
// * We locate term
// * We locate another term
// * And the third one
// * And then we iterate over every subelement

type InvertedIndexItem struct {
	Id   uint32
	Tags []uint32
}

type InvertedIndex struct {
	Thesaurus map[uint32]string
	Forward   map[uint32]*InvertedIndexItem
	Inverted  map[uint32][]*InvertedIndexItem
}

func (i *InvertedIndex) GetItem(id uint32) InvertedIndexItem {

}

// adds item to inverted index
func (i *InvertedIndex) AddItem(id uint32, tags []string) {

}

func (i *InvertedIndex) RemoveItem(id uint32) {

}

func (i *InvertedIndex) IterateAndEditOn(keys []uint32) {

}

// returns list of ids from inverted index
// returns list of possible drills from inverted index
func (i *InvertedIndex) DrillDown(query []string) ([]uint32, []string) {

}

// return keypoint leveled cloud from inverted index.
// The best way to
func (i *InvertedIndex) KeyPointCloud(query []string) map[string][]string {

}
