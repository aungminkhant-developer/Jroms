tinymce.init({
  selector: "textArea#desc",
  init_instance_callback: function (editor) {
    editor.on("keydown", function (e) {
      const errorElement = document.getElementById("description-error");
      if (errorElement != null) {
        errorElement.remove();
      }
    });
  },
  height: 300,
  plugins: [
    "advlist",
    "autolink",
    "link",
    "lists",
    "charmap",
    "prewiew",
    "anchor",
    "pagebreak",
    "searchreplace",
    "wordcount",
    "visualblocks",
    "code",
    "fullscreen",
    "insertdatetime",
    "table",
    "template",
    "codesample",
  ],
  toolbar:
    "undo redo | styles | bold italic underline |link alignleft aligncenter alignright alignjustify |" +
    "bullist numlist outdent indent | fullscreen | ",

  menu: {},
  menubar: "",
  content_style: "body{font-family:Helvetica,Arial,sans-serif; font-size:16px}",
});
tinymce.init({
  selector: "textArea#resp",
  init_instance_callback: function (editor) {
    editor.on("keydown", function (e) {
      const errorElement = document.getElementById("responsibilities-error");
      if (errorElement != null) {
        errorElement.remove();
      }
    });
  },
  height: 300,
  plugins: [
    "advlist",
    "autolink",
    "link",
    "lists",
    "charmap",
    "prewiew",
    "anchor",
    "pagebreak",
    "searchreplace",
    "wordcount",
    "visualblocks",
    "code",
    "fullscreen",
    "insertdatetime",
    "table",
    "template",
    "codesample",
  ],
  toolbar:
    "undo redo | styles | bold italic underline |link alignleft aligncenter alignright alignjustify |" +
    "bullist numlist outdent indent | fullscreen | ",

  menu: {},
  menubar: "",
  content_style: "body{font-family:Helvetica,Arial,sans-serif; font-size:16px}",
});
tinymce.init({
  selector: "textArea#require",
  init_instance_callback: function (editor) {
    editor.on("keydown", function (e) {
      const errorElement = document.getElementById("requirement-error");
      if (errorElement != null) {
        errorElement.remove();
      }
    });
  },
  height: 300,
  plugins: [
    "advlist",
    "autolink",
    "link",
    "lists",
    "charmap",
    "prewiew",
    "anchor",
    "pagebreak",
    "searchreplace",
    "wordcount",
    "visualblocks",
    "code",
    "fullscreen",
    "insertdatetime",
    "table",
    "template",
    "codesample",
  ],
  toolbar:
    "undo redo | styles | bold italic underline |link alignleft aligncenter alignright alignjustify |" +
    "bullist numlist outdent indent | fullscreen | ",

  menu: {},
  menubar: "",
  content_style: "body{font-family:Helvetica,Arial,sans-serif; font-size:16px}",
});
tinymce.init({
  selector: "textArea#pref",
  init_instance_callback: function (editor) {
    editor.on("keydown", function (e) {
      const errorElement = document.getElementById("preferences-error");
      if (errorElement != null) {
        errorElement.remove();
      }
    });
  },
  height: 300,
  plugins: [
    "advlist",
    "autolink",
    "link",
    "lists",
    "charmap",
    "prewiew",
    "anchor",
    "pagebreak",
    "searchreplace",
    "wordcount",
    "visualblocks",
    "code",
    "fullscreen",
    "insertdatetime",
    "table",
    "template",
    "codesample",
  ],
  toolbar:
    "undo redo | styles | bold italic underline |link alignleft aligncenter alignright alignjustify |" +
    "bullist numlist outdent indent | fullscreen | ",

    menu: {

    },
    menubar: '',
    content_style: 'body{font-family:Helvetica,Arial,sans-serif; font-size:16px}'
});

