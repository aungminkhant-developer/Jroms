//for email template
tinymce.init({
    selector: 'textArea#email',
    height: 300,
    plugins:[
        'advlist', 'autolink', 'link','lists', 'charmap', 'prewiew', 'anchor', 'pagebreak',
        'searchreplace', 'wordcount', 'visualblocks', 'code', 'fullscreen', 'insertdatetime',
        'table', 'template', 'codesample'
    ],
    toolbar: 'undo redo  styles  bold italic underline  alignleft aligncenter alignright alignjustify ' +
        'bullist numlist outdent indent  fullscreen ' ,

    menu: {

    },
    menubar: '',
    content_style: 'body{font-family:Helvetica,Arial,sans-serif; font-size:16px}'
});

//for interview send mail 
tinymce.init({
    selector: '#interviewMail',
    height: 300,
    plugins:[
        'advlist', 'autolink', 'link','lists', 'charmap', 'prewiew', 'anchor', 'pagebreak',
        'searchreplace', 'wordcount', 'visualblocks', 'code', 'fullscreen', 'insertdatetime',
        'table', 'template', 'codesample'
    ],
    toolbar: 'undo redo   bold italic underline  alignleft aligncenter alignright alignjustify ' +
        ' outdent indent  fullscreen ' ,

    menu: {

    },
    menubar: '',
    content_style: 'body{font-family:Helvetica,Arial,sans-serif; font-size:16px}'
});