package src;
enum Types {
	Tid(1), Tint(2), Tint64(3), Ttypedef(4), Tmain(5), Tfor(6),
	Tc10int(7),Tc16int(8),Tc10int64(9),Tc16int64(10),
	TlPar(11), TrPar(12), TlBraces(13), TrBraces(14), TlBrackets(15), TrBrackets(16),
	Tsem(17), Tpoint(18), Tcomma(19),
	Tplus(20), Tminus(21), Tinc(22), Tdec(23), Tless(24), Tmore(25), Tmul(26), Tdiv(27),
	TmoreEq(28), TlessEq(29), Teq(30), Tas(31), TnotEq(32), Tmod(33),
	Tend(100), Terr(99);
    private final int value;

    private Types(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
