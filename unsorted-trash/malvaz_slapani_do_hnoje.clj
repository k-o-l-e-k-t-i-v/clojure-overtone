;; Version 1.0beta25
;; http://github.com/overtone/emacs-live
;; hello everyone

(definst bd1
  []
  (let [ton (* (decay:kr (impulse:kr 1/2) 0.2)
               (sin-osc (+ 80 (* 400 (decay:kr (impulse:kr 1/2) 0.04)))))
        nois (* (decay:kr (impulse:kr 1/2) 0.02)
                (white-noise))
        nois (resonz nois (+ 2200 (* 2000 (decay (impulse:kr 1/2) 0.01))) 2.93)
        nois (comb-c nois 2 (+ 0.04 (* 0.032 (mix [ton (lf-noise2:ar 10)]))) 0.9)
        nois (free-verb nois 0.43 0.16 0.8)
        sil (detect-silence ton 0.001 0.6 FREE)
        ton (comb-c ton 2 (+ 0.02 (* (mix [nois ton]) 0.01)) 0.3)
        ton (freq-shift ton (* nois 2000))
        nois (freq-shift:ar nois (* ton 2000))
        ton (free-verb ton 0.43 0.16 0.8)]
    (pan2 (mix [ton nois])
          )))

(bd1)

(def tik (atom 800))
(reset! tik 200)


(swap! tik )

(defn bata [n]
  (at n (do (bd1)
            (reset! tik (choose [800 800/3 800/2 800/6]))))
  (apply-at (+ n (deref tik)) bata (+ n (deref tik)) []))


(stop)

(def m1 (metronome 230))

(bata (now))

(m1)
(m1 (m1))
'tik



(defn bata2 [n]
  (at n (bd1))
  (apply-at (+ n (deref tik)) bata (+ n (deref tik)) []))

(bata2 (now))

(stop)
