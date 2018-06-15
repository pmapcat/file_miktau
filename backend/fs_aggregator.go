package main

// must run after other aggregators are run

// algorithm:
//   * take file under old path
//   * take tags
//   * build new path
//   * if same, skip
//   * if not same, move file into new directory
