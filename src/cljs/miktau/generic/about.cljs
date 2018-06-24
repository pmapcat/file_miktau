(ns miktau.generic.about)

(defn about
  []
  [:div
   [:h1 "About"]
   [:h2 "The reasons behind this system"]
   [:p "In creating this system, we had the following goals in mind"]
   [:ul
    [:li "Help to classify files in the system"]
    [:li "Help to recall existing classifications"]
    [:li "Help to work in bulk with existing file classifications"]]
   
   [:h2 "Tools "]
   [:p "We hoped to achieve these goals with the help of the following principles and tools"]
   [:ul
    [:li [:b "Spatial reasoning"] " The idea behind cloud is to take advantage of human spatial reasoning capabilities, making it possible to remember cloud clearly 
without much problems"]
    [:li [:b "Mechanical memory reasoning"] " Most navigation tasks are accessible through the keyboard and the help of auto completed search"]
    [:li [:b "Providing selection context "] " All of the drilling operations contain operation context"]
    [:li [:b "Bulk renaming "] " The idea is to use bulk renaming to make it easier to rebuild current classification system"]]])
