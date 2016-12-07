(ns gefjun-component-lab.table
    (:require [reagent.core :as reagent]))



;; These 2 probably need revised
(defn ^:private _fsf [x]
	(if (coll? x) 
		(map 
			#(_fsf %)
			x)
		(= :span.search-found x)))

(defn ^:private find-search-found? [x]
	(first (filter true? (flatten (_fsf x)))))


(defn ^:private table-row [columns row search-term]
	(let [formed-row [:tr 
			(map 
				(fn [col index] 
					^{:key index}
					[:td ((col :cell) row search-term)])
				columns
				(range))]]
			(if (or (= 0 (count search-term)) (find-search-found? formed-row)) 
				formed-row 
				nil)))



;; SEARCH Function
;; ----------------------------------------------------------------
;; Usage:
;; 		Put into your :cell function on the data/string you want to search.
;;		If the search-term is found the function surrounds the first match
;;		with a [:span.search-found]
;; ----------------------------------------------------------------
;; Note:
;; 		Let users implement their own search if needed
;; 		The table looks for :span.search-found so if you
;;		create a new search make sure your search outputs it 

(defn search [string search-term]
	(if (and (string? search-term) (not= search-term ""))
		(let [iof (.indexOf (.toLowerCase string) (.toLowerCase search-term))]
			(if (> iof -1) 
				(let [str-len (count search-term)]
					^{:table-show true}
					[:span 
						(subs string 0 iof)
						[:span.search-found (subs string iof (+ iof str-len))] 
						(subs string (+ iof str-len))])
				string))
		string))



;; SORT Function
;; ----------------------------------------------------------------

(defn sort-table [reagent-atom table-path column-index f]
	(let [data (get-in @reagent-atom (conj table-path :data))]
		(swap! reagent-atom
			(constantly (update-in @reagent-atom (conj table-path :data) 
				(constantly (sort-by #(nth % column-index) f data)))))))



(defn ^:private sort-button [reagent-atom table-path index]
	(let [order (reagent/atom :asc)]
		(fn []
			(let [f (get-in @reagent-atom (conj table-path :columns index :sort @order :fn))
		 		  d (get-in @reagent-atom (conj table-path :columns index :sort @order :display))]
		  		(if (not (or (= f nil) (= d nil)))
					[:button.sort 
						{:on-click (fn [] 
							(sort-table reagent-atom table-path index f)
							(swap! order #(if (= @order :asc) :desc :asc)))} d])))))



;; TABLE Component
;; ----------------------------------------------------------------
;; Usage:
;; 		Will generate a table based on user settings
 
(defn table [reagent-atom table-path]
	(let [columns (get-in @reagent-atom (conj table-path :columns))
		  rows (get-in @reagent-atom (conj table-path :data))
		  paging-amount (get-in @reagent-atom (conj table-path :paging :amount))
		  search-term (get-in @reagent-atom (conj table-path :search))]
		[:table
			[:thead>tr
				(map
					(fn [col index]
						^{:key index}
						[:th 
							(col :header) 
							[sort-button reagent-atom table-path index]])
					columns
					(range))]
			[:tbody 
				(take 
					paging-amount
					(map 
						(fn [row index] 
							^{:key index} 
							[table-row columns row search-term])
						rows
						(range)))]]))