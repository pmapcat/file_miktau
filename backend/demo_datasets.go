package main

import (
	"log"
	"math/rand"
	"regexp"
	"strconv"
	"strings"
	"time"
)

type DemoNode struct {
	Tags []string
}

// generate dataset of a larger size (by using markov chains generator)
func generateStressDataSet(input []*CoreNodeItem, amount int) []*CoreNodeItem {
	tags := []string{}
	file_names := []string{}
	fnames_regexp := regexp.MustCompile(`[A-zА-Яа-я\.]+`)
	for _, v := range input {
		tags = append(tags, v.Tags...)
		file_names = append(file_names, fnames_regexp.FindAllString(v.Name, -1)...)
	}
	tagsLorem := markovLorem(strings.Join(tags, " "))
	fnamesLorem := markovLorem(strings.Join(file_names, " "))
	result := []*CoreNodeItem{}
	for i := 0; i <= amount; i++ {
		result = append(result,
			newCoreNodeItemFromDemoDataSet(strconv.Itoa(rand.Intn(1000)), tagsLorem(10), tagsLorem(10), fnamesLorem(10), randomTime()))
	}
	return result
}

func buildDachaDataset() []*CoreNodeItem {
	points := [][]string{
		[]string{"12", "house_plans.docx", "дом забор колодец огород припасы дача сарай беседка сад", "2017-02-26 12:29:44.8628137"},
		[]string{"9", "house_plans_old.docx", "дача дом фундамент погреб стены перекрытия перегородки окна двери крыша		", "	2017-02-24 19:15:01.1267841"},
		[]string{"7", "roofcoating.xlsx", "сарай дача фундамент каркас обшивка крыша стелажи верстак окна дверь		", "	2017-02-24 19:16:14.3330772"},
		[]string{"9", "pipes_house.xlsx", "дача дом лестница кухня санузел вода канализация отопление		", "	2017-02-24 19:20:56.5069633"},
		[]string{"3", "gardening.docx", "сад деревья кусты цветники дача", "2017-02-26 12:31:37.6597189"},
		[]string{"13", "gardening.docx", "дача цветники перед_домом у_дорожки_слева у_дорожки_справа перед_беседкой		", "	2017-02-24 19:24:30.2032258"},
		[]string{"6", "gardening.docx", "дача цветники у_дорожки_слева ближний дальний		", "	2017-02-24 19:26:00.3437904"},
		[]string{"8", "gardening.docx", "дача цветники у_дорожки_справа ближний дальний		", "	2017-02-24 19:26:27.7453298"},
		[]string{"10", "gardening.docx", "дача сад малина		", "	2017-02-24 19:28:42.0356486"},
		[]string{"15", "home_garden_vegetables_fruits.docx", "дача огород план_грядок однолетники многолетники клубника		", "	2017-02-24 19:30:33.1690175"},
		[]string{"12", "home_garden_vegetables_fruits.docx", "дача огород план_грядок план_чередования список_культур		", "	2017-02-24 19:31:22.1054526"},
		[]string{"30", "home_garden_vegetables_fruits.docx", "дача огород список_культур сорта_культур		", "	2017-02-24 19:32:13.9299587"},
		[]string{"35", "house_furniture.docx", "уличная_мебель беседка дача		", "	2017-02-24 19:37:44.7284675"},
		[]string{"43", "house_doors_list.xlsx", "дача двери дом наружные внутренние		", "	2017-02-24 19:45:41.6331649"},
		[]string{"12", "house_warming.docx", "дача двери дом наружные конструкция утепление  уплотнение замки ручки		", "	2017-02-24 19:46:45.4088209"},
		[]string{"1", "house_blueprints.docx", "дача двери дом наружные конструкция эскизы чертежи потребные_материалы		", "	2017-02-24 19:49:24.8236399"},
		[]string{"34", "house_water_supply.docx", "дача колодец насос обратный_клапан шланг_нижний шланг_верхний ворот ведро трос_ведра каркас_под_насос тросы_подвеса_насоса		", "	2017-02-24 19:54:49.2490724"},
		[]string{"9", "house_furniture.xlsx", "стулья холодильник стол тумбы дача кухня полки дом плита газовый_балон		", "	2017-02-24 20:38:40.2114795"},
		[]string{"0", "house_kitchen_pipes.xlsx", "кухня дача дом раковина		", "	2017-02-24 20:01:19.5990267"},
		[]string{"43", "house_pipes.xlsx", "дача дом санузел		", "	2017-02-24 20:02:47.4010493"},
		[]string{"23", "house_pipes.xlsx", "дача дом санузел туалет ванная		", "	2017-02-24 20:03:52.9450861"},
		[]string{"37", "house_pipes.xlsx", "дача дом санузел ванная раковина душ ванна		", "	2017-02-24 20:04:37.6170127"},
		[]string{"29", "house_pipes_water.xlsx", "на_кухню к_унитазу дача к_ванне дом к_душу вода ввод разводка_воды_в_доме		", "	2017-02-24 20:25:59.6308522"},
		[]string{"3", "house_pipes_water.xlsx", "магистраль_в доме магистраль_до_колодца вывод_из_дома дача от_раковины_в_ванной от_раковины_в_туалете фильтрационный_колодец дом кухни от_раковины_с канализация 		", "	2017-02-24 20:15:53.0201422"},
		[]string{"3", "house_pipes_water.xlsx", "дача дом разводка_воды_в_доме к_раковине_в_туалете к_душу		", "	2017-02-24 20:25:31.757016"},
		[]string{"9", "house_pipes_water.xlsx", "разводка_воды_в_доме дача дом к_раковине_в_ванной		", "	2017-02-24 20:24:44.772827"},
		[]string{"12", "supplies.docx", "дача припасы		", "	2017-02-24 20:39:39.8927669"},
		[]string{"10", "locks.docx", "сарай дача дверь замок ручки		", "2017-02-26 12:57:44.2205109"},
		[]string{"3", "house_doors_and_locks.xlsx", "дача дом двери наружные замки", "2017-02-26 13:04:30.610489"},
		[]string{"5", "house_doors_and_locks.xlsx", "дача дом двери наружные ручки", "2017-02-26 13:04:58.1447787"},
		[]string{"6", "house_doors_and_locks.xlsx", "дача дом двери наружные утепление	", "2017-02-26 13:06:03.5838725"},
		[]string{"8", "house_doors_and_locks.xlsx", "дача дом двери наружные уплотнение", "2017-02-26 13:07:03.0331211"},
		[]string{"10", "house_attic.xlsx", "дача беседка гриль сушилка", "2017-02-26 13:14:14.7862805"},
		[]string{"9", "house_attic.xlsx", "дача дом погреб", "2017-02-26 15:05:51.8877951"},
		[]string{"8", "house_attic.xlsx", "дача дом погреб стены пол стелажи освещение", "2017-02-26 15:06:31.3108636"},
		[]string{"6", "house_attic.xlsx", "дача дом погреб стены потребные_материалы", "2017-02-26 15:11:52.6173912"},
		[]string{"3", "house_doors_and_locks.xlsx", "дом дача двери наружные цвет?", "2017-02-26 15:22:25.5941292"},
	}
	result := []*CoreNodeItem{}
	for _, v := range points {
		fsize, filename, tags, date := v[0], v[1], v[2], v[3]
		t, err := time.Parse("2006-01-02 15:04:05.9999999", strings.TrimSpace(date))
		if err != nil {
			log.Fatal(err)
		}
		result = append(result, newCoreNodeItemFromDemoDataSet(fsize, "/home/mik/dacha/", tags, filename, t))
	}
	return result
}

func buildDemoDataset() []*CoreNodeItem {
	points := [][]string{
		[]string{"87", "/home/mik/this_must_be_it/", "2016.07.21", "blab.mp4", "", "пустая запись"},
		[]string{"34", "/home/mik/this_must_be_it/", "2017.07.20", "hello.mp4", "natan work bibliostore moscow_market", "удалить таблицу alib_full_book_en(и слить результаты)"},
		[]string{"123", "/home/mik/this_must_be_it/", "2018.07.19", "blab.mp4", "natan work bibliostore moscow_market", "сделать autosuggester"},
		[]string{"456", "/home/mik/this_must_be_it/", "2016.07.18", "glib.mp4", "natan work bibliostore moscow_market", "сделать autosuggester"},
		[]string{"324", "/home/mik/chosen_one/", "2017.02.17", "blob.mp4", "natan work bibliostore translator", "использовать новоприобретенный(быстрый) mosesdecoder"},
		[]string{"462", "/home/mik/chosen_one/", "2018.02.16", "plop.mp4", "natan work bibliostore moscow_market", "исключить dixidu из списка книг(перекачать их на новый лад) "},
		[]string{"8", "/home/mik/chosen_one/", "2016.02.15", "grop.mp4", "natan work bibliostore moscow_market", "исключить dixidu из списка книг "},
		[]string{"34", "/home/mik/chosen_one/", "2016.02.14", "drop.mp4", "natan work moscow_market amazon", "сформировать uie для amazon"},
		[]string{"28", "/home/mik/this_must_be_it/", "2016.04.13", "nrap.mp4", "natan work devops moscow_market bibliostore translator amazon", "поднять CI сервер(для того чтобы перезагрузка данных шла быстрее)"},
		[]string{"12", "/home/mik/another/", "2017.04.12", "zlip.mp4", "natan work wiki sforim согласовать", "сделать шаблон вики по базе сфорим"},
		[]string{"9", "/home/mik/another/", "2017.04.11", "zlop.mp4", "natan work moscow_market скачка_источников биржа", "мешок/авито/алиб"},
		[]string{"3", "/home/mik/another/", "2017.04.10", "zip.mp4", "natan work магазины sforim", "сделать простой магазин для сфорим(на woocommerce)"},
		[]string{"6", "/home/mik/another/", "2016.02.09", "nop.mp4", "natan work магазины moscow_market bibliostore", "сделать магазин для книг московского рынка(bibliostore)"},
		[]string{"8", "/home/mik/another/", "2016.02.08", "nar.mp4", "natan work биржа UI", "сделать мокапы пользовательского интерфейса"},
		[]string{"4", "/home/mik/another/", "2016.02.07", "gor.mp4", "work personal", "Сделать этот проект"},
		[]string{"5", "/home/mik/figuratively/", "2016.02.05", "dar.mp4", "work personal blog", "Начать вести блог"},
		[]string{"3", "/home/mik/figuratively/", "2016.05.05", "gir.mp4", "work personal usecases", "Сделать landing с юз-кейсами(то что я сделал)  "},
		[]string{"2", "/home/mik/figuratively/", "2016.05.04", "grar.mp4", "work personal usecases", "Сделать этот проект"},
		[]string{"7", "/home/mik/figuratively/literally", "2016.05.03", "grion.mp4", "work zeldin ", "Собери же  cropper наконец"},
		[]string{"2", "/home/mik/figuratively/literally", "2016.05.02", "grano.mp4", "work zeldin ", "Этот проект ему(в принципе) тоже понравится"},
		[]string{"6", "/home/mik/figuratively/literally", "2016.03.01", "dramo.mp4", "work everybook ", "Общайтесь с Жанной. Вытягивайте денюжку"},
		[]string{"12", "/home/mik/figuratively/", "2017.01.24", "blab.mp4", "работа_сделана ", "сделал парсер bidspirit(так-же починил проблему с форматом estimate) обнаружил возможности оптимизации хорошего переводчика(случайно) попробовал исполь его при названиях на bidspirit(по этому парсинг bidspirit затянулся) Однако в перспективе можно использовать хороший перевод"}}
	result := []*CoreNodeItem{}
	for _, v := range points {
		fsize, fpath, date, filename, tags := v[0], v[1], v[2], v[3], v[4]
		t, err := time.Parse("2006.01.02", strings.TrimSpace(date))
		if err != nil {
			log.Fatal("Error on: ", date, err)
		}
		result = append(result, newCoreNodeItemFromDemoDataSet(fsize, fpath, tags, filename, t))
	}
	return result
}
