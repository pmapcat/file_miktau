(ns miktau.generic.about
  (:require [miktau.app-metadata :refer [app-metadata]]
            [miktau.generic.tos :as tos]
            ))
(defn components
  []
  [:div
   [:h2 "Open source components"]
   [:p "We've built the software with the help of the following open source components"]
   [:ul
    [:li [:a {:href "https://electronjs.org/"} "Electron"] "  Build cross platform desktop apps with JavaScript, HTML, and CSS "]
    [:li [:a {:href "https://golang.org/"} "Go"] " is an open source programming language that makes it easy to build simple, reliable, and efficient software."]
    [:li [:a {:href "https://clojurescript.org/"} "ClojureScript"] " is a robust, practical, and fast programming language with a set of useful features that together form a simple, coherent, and powerful tool."]
    [:li [:a {:href "https://github.com/Day8/re-frame"} "Reframe"] " A Reagent Framework For Writing SPAs, in Clojurescript."]
    [:li [:a {:href "https://reactjs.org/"} "React"] " A JavaScript library for building user interfaces"]
    [:li [:a {:href "https://reagent-project.github.io/"} "Reagent" ] " Minimalistic React for ClojureScript"]
    
    [:li [:a {:href "https://purecss.io/"} "PureCSS"] " A set of small, responsive CSS modules that you can use in every web project."]
    [:li [:a {:href "https://github.com/noprompt/garden"} "Garden "] "  Generate CSS with Clojure"]
    [:li " and mutliple others"]]])

(defn about
  []
  [:div
   [:h1 "About"]
   [:p
    [:b "Product version: " ] (:version app-metadata) [:br]
    [:b "Contact address: "]  (:email app-metadata) [:br]
    (:company-name app-metadata)]
   
   [:h2 "The reasons behind building this system"]
   [:p "In creating this system, we had the following goals in mind"]
   [:ul
    [:li "Help classify files in the system"]
    [:li "Help recall existing classifications"]
    [:li "Help work in bulk with existing file classifications"]]
   
   [:h2 "Tools "]
   [:p "We hoped to achieve these goals with the help of the following principles and tools"]
   [:ul
    [:li [:b "Spatial reasoning"] " The idea behind cloud is to take advantage of human spatial reasoning capabilities, making it possible to remember cloud clearly 
without much problems"]
    [:li [:b "Mechanical memory reasoning"] " Most navigation tasks are accessible through the keyboard and the help of auto completed search"]
    [:li [:b "Providing selection context "] " All of the drilling operations contain operation context"]
    [:li [:b "Bulk renaming "] " The idea is to use bulk renaming to make it easier to change current classification system"]]
   [components]
   [tos/eula]])
