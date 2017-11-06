import groovy.xml.MarkupBuilder

def pw = new StringWriter()
def html = new MarkupBuilder(pw)

def payload = [
    [mat_id: 1234, name: "Material", units: 12, measure: "kg", threshold: 20]
]

html.html {
    head {
        meta("http-equiv": "Content-Type", content: "text/html")
        style(type: "text/css") { mkp.yield("body {font-family:'Arial',sans-serif;} h1 {color: #F39323;} table {border-collapse: collapse;} th {text-align: left; background: #575756; padding: 4px 20px 4px 4px; color: white; border: 1px solid #575756;} td {padding: 4px 20px 4px 4px; border: 1px solid #575756;}") }
    }
    body {
        h1("Stocks by material threshold report")
        table {
            thead {
                tr {
                    th("Material ID")
                    th("Name")
                    th("Units")
                    th("Threshold")
                }
            }
            tbody {
                payload.each { row ->
                    tr {
                        td(row.mat_id)
                        td(row.name)
                        td(row.units + " " + row.measure)
                        td(row.threshold)
                    }
                }
            }
        }
    }
}

println pw.toString()