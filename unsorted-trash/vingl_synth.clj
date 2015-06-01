;; Version 1.0beta25
;; http://github.com/overtone/emacs-live
;; hello everyone


(demo 100
      (out 0
           (pan2 (* (env-gen (envelope [0 1 1 0] [10 80 10] [-2 0 6]) 1 1 0 1 FREE)
                    (normalizer (free-verb (rlpf (apply + (saw (* (line:kr 80 85 100)
                                                                  [2.01 3.09 6.997])))
                                              (lin-exp (lf-tri (lin-exp (lf-noise2 1) -1 1 4 20)) -1 1 40 2000)
                                               (lin-exp (lf-noise2 2) -1 1 0.71 4.9))
                                         0.5 0.992 0.8)))
                 0 1)))
(stop)

(definst vingl
  [note     60
   peak     1
   sustain  0.9
   a        0.7
   d        0.63
   r        0.3
   dur      0.4
   facc     20
   fcrv     2
   fs       2
   fltlo    40
   flthi    2000
   qs       10
   qlo      0.27
   qhi      2.3
   verbmix  0.395
   verbsiz  0.96
   verbdmp  0.93
   ps       0.3]
  (let [freq (midicps note)
        aenv (env-gen (envelope [0 peak sustain sustain 0] [a d dur r]) 1 1 0 1)
        fenv (env-gen (perc 0 a facc fcrv) 1 1 freq 1)
        fch  (lin-exp (lf-noise2 fs) -1 1 fltlo flthi)
        qch  (apply + (lin-exp (lf-noise2 (* qs [1.01 2.3 4.03])) -1 1 qlo qhi))
        src  (apply + (saw (* fenv [1.003 2.001 2.9997 4.0999])))
        flt1 (rlpf src fch qch)
        flt2 (apply + (bpf src (* [0.25 0.5 1.09 2.13] fch) (* [1 0.2 0.3] qch)))
        src  (+ flt1 flt2)
        src  (normalizer src)
        src  (* aenv src)
        src  (free-verb src verbmix verbsiz verbdmp)
        sil  (detect-silence src 0.0001 3 FREE)
        pant (- (lin-exp (lf-noise2 ps) -1 1 0.3 0.7) 0.5)
        ]
    (out 0 (pan2 src pant 1))))

(map
 (fn [x] (vingl (note x) :dur 2 :a 10 :r 5 :d 2 :peak 0.15 :sustain 0.25 :fs (rand 6) :qs (rand 6) :ps (rand 8)))
 (take 6 (shuffle [:c1 :d#2 :e2 :c#1 :c#4 :d#4]) ))

(defn hurl []
  (map
 (fn [x] (vingl (note x) :dur 0 :a 1 :r 0.73 :d 2 :peak 0.15 :sustain 0.25 :fs (rand 6) :qs (rand 6) :ps (rand 8)))
 (take 4 (shuffle [:c1 :f#2 :e3 :c#1 :c#2 :d#4]) )))

(hurl)
(def metro (metronome 280))
(metro)


(defn hurl-seq [t]
  (at (now) (hurl))
  (apply-by t #'hurl t [])
  )

(hurl-seq 8000 )
(stop)

(stop)
(defn ving [x]
  (vingl (note x) :dur 0.1 :a 0 :r 0.15 :sustain 0.1 :fs 0 :qs 0 :ps 8))

(def seq-1 (cycle [:c2 :c2 :d2 :g2 :d#2 :a#1]))
;(println seq-1)

(def metro (metronome 280))

(defn b [t r notes]
  (at (now) (ving (first notes)))
  (apply-at (+ (now) t) #'b t r (next notes) )
  )

(metro)
(def b1 (b 1000 0 seq-1))
(stop)
