(ns miktau.lorem
  (:require  [clojure.string :as cljs-string]))

(def lorem-ipsum
  "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?")

(def sentences
  (cljs-string/split lorem-ipsum #"(?<=[.?]) "))

(defn random-sentence
  ([] (random-sentence 1))
  ([n] (take n (repeatedly #(nth sentences (rand-int (count sentences)))))))

(defn random-word
  ([] (random-word 1))
  ([n] (shuffle (filter (comp not empty?) (take n (cljs-string/split  lorem-ipsum  #"[, ]"))))))
(defn random-word-random-size
  [max]
  (random-word (rand-nth (rest (range max)))))

(defn random-paragraph
  ([] (random-paragraph 1))
  ([n] (take n (repeatedly #(cljs-string/join \space (random-sentence 5))))))
