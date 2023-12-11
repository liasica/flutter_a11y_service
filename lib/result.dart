class Result {
  Event? event;
  List<Nodes>? nodes;

  Result({
    this.event,
    this.nodes,
  });

  Result copyWith({
    Event? event,
    List<Nodes>? nodes,
  }) {
    return Result(
      event: event ?? this.event,
      nodes: nodes ?? this.nodes,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'event': event,
      'nodes': nodes,
    };
  }

  factory Result.fromJson(Map<String, dynamic> json) {
    return Result(
      event: json['event'] == null ? null : Event.fromJson(Map.from(json['event'])),
      nodes: (json['nodes'] as List<dynamic>?)?.map((e) => Nodes.fromJson(Map.from(e))).toList(),
    );
  }

  @override
  String toString() {
    String str = '\n---↓↓↓ RESULT ↓↓↓---\nEVENT:\n$event\nNODES:\n';
    nodes?.forEach((e) {
      str += '$e\n';
    });
    str += '\n---↑↑↑ RESULT ↑↑↑---\n';
    return str;
  }

  @override
  int get hashCode => Object.hash(event, nodes);

  @override
  bool operator ==(Object other) => identical(this, other) || other is Result && runtimeType == other.runtimeType && event == other.event && nodes == other.nodes;
}

class Event {
  String? packageName;
  String? className;
  String? text;
  String? description;

  Event({
    this.packageName,
    this.className,
    this.text,
    this.description,
  });

  Event copyWith({
    String? packageName,
    String? className,
    String? text,
    String? description,
  }) {
    return Event(
      packageName: packageName ?? this.packageName,
      className: className ?? this.className,
      text: text ?? this.text,
      description: description ?? this.description,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'packageName': packageName,
      'className': className,
      'text': text,
      'description': description,
    };
  }

  factory Event.fromJson(Map<String, dynamic> json) {
    return Event(
      packageName: json['packageName'] as String?,
      className: json['className'] as String?,
      text: json['text'] as String?,
      description: json['description'] as String?,
    );
  }

  @override
  String toString() => '$packageName / $className { $text | $description }';

  @override
  int get hashCode => Object.hash(packageName, className, text, description);

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is Event && runtimeType == other.runtimeType && packageName == other.packageName && className == other.className && text == other.text && description == other.description;
}

class Nodes {
  List<int>? depth;
  List<int>? bounds;
  String? id;
  String? packageName;
  String? className;
  String? text;
  String? description;
  bool? clickable;
  bool? scrollable;
  bool? editable;

  Nodes({
    this.depth,
    this.bounds,
    this.id,
    this.packageName,
    this.className,
    this.text,
    this.description,
    this.clickable,
    this.scrollable,
    this.editable,
  });

  Nodes copyWith({
    List<int>? depth,
    List<int>? bounds,
    String? id,
    String? packageName,
    String? className,
    String? text,
    String? description,
    bool? clickable,
    bool? scrollable,
    bool? editable,
  }) {
    return Nodes(
      depth: depth ?? this.depth,
      bounds: bounds ?? this.bounds,
      id: id ?? this.id,
      packageName: packageName ?? this.packageName,
      className: className ?? this.className,
      text: text ?? this.text,
      description: description ?? this.description,
      clickable: clickable ?? this.clickable,
      scrollable: scrollable ?? this.scrollable,
      editable: editable ?? this.editable,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'depth': depth,
      'bounds': bounds,
      'id': id,
      'packageName': packageName,
      'className': className,
      'text': text,
      'description': description,
      'clickable': clickable,
      'scrollable': scrollable,
      'editable': editable,
    };
  }

  factory Nodes.fromJson(Map<String, dynamic> json) {
    return Nodes(
      depth: (json['depth'] as List<dynamic>?)?.map((e) => e as int).toList(),
      bounds: (json['bounds'] as List<dynamic>?)?.map((e) => e as int).toList(),
      id: json['id'] as String?,
      packageName: json['packageName'] as String?,
      className: json['className'] as String?,
      text: json['text'] as String?,
      description: json['description'] as String?,
      clickable: json['clickable'] as bool?,
      scrollable: json['scrollable'] as bool?,
      editable: json['editable'] as bool?,
    );
  }

  @override
  String toString() => '$depth : $packageName / $className { $id | $text | $description | $bounds | $clickable | $scrollable | $editable }';

  @override
  int get hashCode => Object.hash(depth, bounds, id, packageName, className, text, description, clickable, scrollable, editable);

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is Nodes &&
          runtimeType == other.runtimeType &&
          depth == other.depth &&
          bounds == other.bounds &&
          id == other.id &&
          packageName == other.packageName &&
          className == other.className &&
          text == other.text &&
          description == other.description &&
          clickable == other.clickable &&
          scrollable == other.scrollable &&
          editable == other.editable;
}
