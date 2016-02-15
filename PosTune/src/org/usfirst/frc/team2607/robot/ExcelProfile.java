package org.usfirst.frc.team2607.robot;

// goes from 0 to 100 (one turn of output shaft)

public class ExcelProfile {			
	public static final int kNumPoints =179;		
	// Position (rotations)	Velocity (RPM)	Duration (ms)
	public static double [][]Points = new double[][]{		
	{0,	0	,10},
	{0.00061859756097561,	7.423170732	,10},
	{0.00278368902439024,	18.55792683	,10},
	{0.00711387195121951,	33.40426829	,10},
	{0.014227743902439,	51.96219512	,10},
	{0.0247439024390244,	74.23170732	,10},
	{0.0392809451219512,	100.2128049	,10},
	{0.0584574695121951,	129.9054878	,10},
	{0.0828920731707317,	163.3097561	,10},
	{0.113203353658537,	200.4256098	,10},
	{0.150009908536585,	241.2530488	,10},
	{0.193930335365854,	285.7920732	,10},
	{0.245583231707317,	334.0426829	,10},
	{0.305587195121951,	386.004878	,10},
	{0.374560823170732,	441.6786585	,10},
	{0.453122713414634,	501.0640244	,10},
	{0.541891463414634,	564.1609756	,10},
	{0.641485670731707,	630.9695122	,10},
	{0.752523932926829,	701.4896341	,10},
	{0.875624847560976,	775.7213415	,10},
	{1.01140701219512,	853.6646341	,10},
	{1.16048902439024,	935.3195122	,10},
	{1.32348948170732,	1020.685976	,10},
	{1.50102698170732,	1109.764024	,10},
	{1.69372012195122,	1202.553659	,10},
	{1.9021875,	1299.054878	,10},
	{2.12704771341463,	1399.267683	,10},
	{2.3689193597561,	1503.192073	,10},
	{2.62842103658537,	1610.828049	,10},
	{2.90617134146341,	1722.17561	,10},
	{3.20278887195122,	1837.234756	,10},
	{3.51889222560976,	1956.005488	,10},
	{3.8551,	2078.487805	,10},
	{4.21203079268293,	2204.681707	,10},
	{4.59030320121951,	2334.587195	,10},
	{4.99053582317073,	2468.204268	,10},
	{5.41334725609756,	2605.532927	,10},
	{5.85935609756098,	2746.573171	,10},
	{6.32918094512195,	2891.325	,10},
	{6.82344039634147,	3039.788415	,10},
	{7.34275304878049,	3191.963415	,10},
	{7.88680960365854,	3336.715244	,10},
	{8.45468216463415,	3477.755488	,10},
	{9.04575213414634,	3615.084146	,10},
	{9.65940091463415,	3748.70122	,10},
	{10.2950099085366,	3878.606707	,10},
	{10.9519605182927,	4004.80061	,10},
	{11.6296341463415,	4127.282927	,10},
	{12.327412195122,	4246.053659	,10},
	{13.0446760670732,	4361.112805	,10},
	{13.7808071646342,	4472.460366	,10},
	{14.5351868902439,	4580.096341	,10},
	{15.3071966463415,	4684.020732	,10},
	{16.0962178353659,	4784.233537	,10},
	{16.9016318597561,	4880.734756	,10},
	{17.7228201219512,	4973.52439	,10},
	{18.5591640243903,	5062.602439	,10},
	{19.4100449695122,	5147.968902	,10},
	{20.2748443597561,	5229.62378	,10},
	{21.152943597561,	5307.567073	,10},
	{22.0437240853659,	5381.79878	,10},
	{22.9465672256098,	5452.318902	,10},
	{23.8608544207317,	5519.127439	,10},
	{24.7859670731707,	5582.22439	,10},
	{25.7212865853659,	5641.609756	,10},
	{26.6661943597561,	5697.283537	,10},
	{27.6200717987805,	5749.245732	,10},
	{28.5823003048781,	5797.496341	,10},
	{29.5522612804878,	5842.035366	,10},
	{30.5293361280488,	5882.862805	,10},
	{31.51290625,	5919.978659	,10},
	{32.5023530487805,	5953.382927	,10},
	{33.4970579268293,	5983.07561	,10},
	{34.4964022865854,	6009.056707	,10},
	{35.4997675304878,	6031.32622	,10},
	{36.5065350609756,	6049.884146	,10},
	{37.5160862804878,	6064.730488	,10},
	{38.5278025914634,	6075.865244	,10},
	{39.5410653963415,	6083.288415	,10},
	{40.555256097561,	6087	,10},
	{41.569756097561,	6087	,10},
	{42.584256097561,	6087	,10},
	{43.598756097561,	6087	,10},
	{44.613256097561,	6087	,10},
	{45.627756097561,	6087	,10},
	{46.642256097561,	6087	,10},
	{47.656756097561,	6087	,10},
	{48.671256097561,	6087	,10},
	{49.685756097561,	6087	,10},
	{50.700256097561,	6087	,10},
	{51.714756097561,	6087	,10},
	{52.729256097561,	6087	,10},
	{53.743756097561,	6087	,10},
	{54.758256097561,	6087	,10},
	{55.772756097561,	6087	,10},
	{56.787256097561,	6087	,10},
	{57.801756097561,	6087	,10},
	{58.816256097561,	6087	,10},
	{59.830756097561,	6087	,10},
	{60.8452560975609,	6087	,10},
	{61.8591375,	6079.576829	,10},
	{62.8714724085366,	6068.442073	,10},
	{63.8816422256097,	6053.595732	,10},
	{64.8890283536585,	6035.037805	,10},
	{65.8930121951219,	6012.768293	,10},
	{66.892975152439,	5986.787195	,10},
	{67.8882986280487,	5957.094512	,10},
	{68.8783640243902,	5923.690244	,10},
	{69.8625527439024,	5886.57439	,10},
	{70.8402461890244,	5845.746951	,10},
	{71.8108257621951,	5801.207927	,10},
	{72.7736728658536,	5752.957317	,10},
	{73.728168902439,	5700.995122	,10},
	{74.6736952743902,	5645.321341	,10},
	{75.6096333841463,	5585.935976	,10},
	{76.5353646341463,	5522.839024	,10},
	{77.4502704268292,	5456.030488	,10},
	{78.3537321646341,	5385.510366	,10},
	{79.24513125,	5311.278659	,10},
	{80.1238490853658,	5233.335366	,10},
	{80.9892670731707,	5151.680488	,10},
	{81.8407666158536,	5066.314024	,10},
	{82.6777291158536,	4977.235976	,10},
	{83.4995359756097,	4884.446341	,10},
	{84.3055685975609,	4787.945122	,10},
	{85.0952083841463,	4687.732317	,10},
	{85.8678367378048,	4583.807927	,10},
	{86.6228350609755,	4476.171951	,10},
	{87.3595847560975,	4364.82439	,10},
	{88.0774672256097,	4249.765244	,10},
	{88.7758638719512,	4130.994512	,10},
	{89.4541560975609,	4008.512195	,10},
	{90.111725304878,	3882.318293	,10},
	{90.7479528963414,	3752.412805	,10},
	{91.3622202743902,	3618.795732	,10},
	{91.9539088414633,	3481.467073	,10},
	{92.5223999999999,	3340.426829	,10},
	{93.067075152439,	3195.675	,10},
	{93.5873157012195,	3047.211585	,10},
	{94.0825030487804,	2895.036585	,10},
	{94.5529464939024,	2750.284756	,10},
	{94.9995739329268,	2609.244512	,10},
	{95.4230039634146,	2471.915854	,10},
	{95.8238551829268,	2338.29878	,10},
	{96.2027461890243,	2208.393293	,10},
	{96.5602955792682,	2082.19939	,10},
	{96.8971219512194,	1959.717073	,10},
	{97.213843902439,	1840.946341	,10},
	{97.5110800304877,	1725.887195	,10},
	{97.7894489329268,	1614.539634	,10},
	{98.049569207317,	1506.903659	,10},
	{98.2920594512194,	1402.979268	,10},
	{98.5175382621951,	1302.766463	,10},
	{98.7266242378048,	1206.265244	,10},
	{98.9199359756097,	1113.47561	,10},
	{99.0980920731707,	1024.397561	,10},
	{99.2617111280487,	939.0310976	,10},
	{99.4114117378048,	857.3762195	,10},
	{99.5478124999999,	779.4329268	,10},
	{99.6715320121951,	705.2012195	,10},
	{99.7831888719512,	634.6810976	,10},
	{99.8834016768292,	567.872561	,10},
	{99.9727890243902,	504.7756098	,10},
	{100.051969512195,	445.3902439	,10},
	{100.121561737805,	389.7164634	,10},
	{100.18218429878,	337.7542683	,10},
	{100.234455792683,	289.5036585	,10},
	{100.278994817073,	244.9646341	,10},
	{100.316419969512,	204.1371951	,10},
	{100.347349847561,	167.0213415	,10},
	{100.37240304878,	133.6170732	,10},
	{100.392198170732,	103.9243902	,10},
	{100.407353810976,	77.94329268	,10},
	{100.418488567073,	55.67378049	,10},
	{100.426221036585,	37.11585366	,10},
	{100.431169817073,	22.2695122	,10},
	{100.433953506097,	11.1347561	,10},
	{100.435190701219,	3.711585366	,10},
	{100.4355,	0	,10}};
}

