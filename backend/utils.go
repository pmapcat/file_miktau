package main

import (
	"math/rand"
	"strings"
)

func randomDateField() CoreDateField {
	return CoreDateField{
		Month: rand.Intn(12-1) + 1,
		Year:  rand.Intn(2018-2010) + 2010,
		Day:   rand.Intn(31-1) + 1,
	}
}

// will generate random (lorem ipsum) text based on
// input and output distribution
func markovLorem(input string) func(outlen int) string {
	var_arr := strings.Split(input, " ")
	// it is easier to have rand_nth on a list with dublicates than doing
	// non uniform random over a distribution in hashmap

	// btw, I have no idea whether I should shuffle res
	// after using the algorithm.
	res := map[string][]string{}
	for i := 1; i < len(var_arr); i++ {
		item, ok := res[var_arr[i-1]]
		if !ok {
			res[var_arr[i-1]] = []string{}
		}
		item = append(item, var_arr[i])
	}
	return func(maxlen int) string {
		return_data := []string{var_arr[rand.Intn(len(var_arr))]}
		for i := 0; i <= maxlen; i++ {
			seed := return_data[len(return_data)-1]
			return_data = append(return_data, res[seed][rand.Intn(len(res[seed]))])
		}
		return strings.Join(return_data, " ")
	}
}
