(ns gefjun-component-lab.core
    (:require [reagent.core :as reagent]
    		  [gefjun-component-lab.table :as gefjun-table]))

;; -------------------------
;; App State

(def app-state
	(reagent/atom {

		:table-configuration {
			:data (take 1000 (cycle [
						["James" "C" "Blake"]
						["Mark" "M" "Smith"]
						["Cecil" "L" "Jackson"]]))

			:columns [
				{:header [:em "First"] 
				 :cell (fn [row search] [:em (gefjun-table/search (first row) search)])}
				{:header "MI"
				 :cell (fn [row] (second row))
				 :sort {:asc {:fn > :display "asc"}
				 		:desc {:fn < :display "desc"}}}
				{:header "Last"
				 :cell (fn [row] (last row))
				 :sort {:asc {:fn > :display "asc"}
				 		:desc {:fn < :display "desc"}}}]

			:paging {
				:offset 0
				:amount 100}

			:search ""

		}}))

;; ------------------------
;; Helper functions

(defn update-state! [sp f & xs]
	(apply swap! app-state update-in sp f xs))

;; -------------------------
;; Views

(defn home-page []
  [:div 
  	[:h2 "gefjun-component-lab test"]
  	[:br]
  	[:h4 "table test"]
  	[:div
  		[:input {:type "text" :on-change 
  			(fn [e] 
				(update-state! [:table-configuration :search] (constantly (.. e -target -value))))}]
  		[gefjun-table/table app-state [:table-configuration]]]])

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
