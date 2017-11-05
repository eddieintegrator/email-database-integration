import groovy.xml.MarkupBuilder

def pw = new StringWriter()
def html = new MarkupBuilder(pw)

html.div {
    h1("Stocks by material report")
    table(border: 1) {
        thead {
            tr {
                th("Material ID")
                th("Name")
                th("Units")
            }
            tbody {
                payload.each { row ->
                    tr {
                        td(row.mat_id)
                        td(row.name)
                        td(row.units + " " + row.measure)
                    }
                }
            }
        }
    }
}

payload = pw.toString()