;; Version 1.0beta25
;; http://github.com/overtone/emacs-live
;; hello everyone

(defn normalize-vector
  [vc]
  (let [suma          (apply + vc)
        d             (fn [x] (/ x suma))
        normalized    (into [] (map d vc))]
    normalized))

(normalize-vector [1 4 5 16])



(map (fn [x] (+ 3 x)) (vector 1 2 3 4))
